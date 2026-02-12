package br.com.tlmacedo.minhasfinancas.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.tlmacedo.minhasfinancas.data.local.dao.ContaComTipo
import br.com.tlmacedo.minhasfinancas.data.local.entity.Conta
import br.com.tlmacedo.minhasfinancas.data.local.entity.TipoConta
import br.com.tlmacedo.minhasfinancas.data.repository.ContaRepository
import br.com.tlmacedo.minhasfinancas.data.repository.TipoContaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ContasViewModel"

data class ContasUiState(
    val contas: List<ContaComTipo> = emptyList(),
    val tiposConta: List<TipoConta> = emptyList(),
    val saldoTotal: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null
)

data class ContaFormState(
    val id: Long = 0,
    val nome: String = "",
    val tipoContaId: Long? = null,
    val saldoInicial: Long = 0,
    val cor: String = "#4CAF50",
    val icone: String? = "account_balance_wallet",
    val bancoId: String? = null,
    val usarIconeBanco: Boolean = false,
    val incluirNoTotal: Boolean = true,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ContasViewModel @Inject constructor(
    private val contaRepository: ContaRepository,
    private val tipoContaRepository: TipoContaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContasUiState())
    val uiState: StateFlow<ContasUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(ContaFormState())
    val formState: StateFlow<ContaFormState> = _formState.asStateFlow()

    init {
        loadContas()
        loadTiposConta()
    }

    private fun loadContas() {
        viewModelScope.launch {
            try {
                contaRepository.getAllContasComTipo().collect { contas ->
                    val saldoTotal = contas
                        .filter { it.conta.incluirNoTotal && it.conta.ativa }
                        .sumOf { it.conta.saldoAtual }
                    _uiState.update { it.copy(contas = contas, saldoTotal = saldoTotal, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadTiposConta() {
        viewModelScope.launch {
            try {
                tipoContaRepository.getAllTiposConta().collect { tipos ->
                    _uiState.update { it.copy(tiposConta = tipos, isLoading = false) }
                    if (_formState.value.tipoContaId == null && tipos.isNotEmpty()) {
                        _formState.update { it.copy(tipoContaId = tipos.first().id) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateNome(nome: String) {
        _formState.update { it.copy(nome = nome, error = null) }
    }

    fun updateTipoConta(tipoContaId: Long) {
        _formState.update { it.copy(tipoContaId = tipoContaId) }
    }

    fun updateSaldoInicial(centavos: Long) {
        _formState.update { it.copy(saldoInicial = centavos) }
    }

    fun updateCor(cor: String) {
        _formState.update { it.copy(cor = cor) }
    }

    fun updateIcone(icone: String?) {
        _formState.update { it.copy(icone = icone, bancoId = null, usarIconeBanco = false) }
    }

    fun updateBancoId(bancoId: String?) {
        _formState.update { it.copy(bancoId = bancoId, icone = null, usarIconeBanco = true) }
    }

    fun setUsarIconeBanco(usar: Boolean) {
        _formState.update { state ->
            if (usar) state.copy(usarIconeBanco = true, icone = null)
            else state.copy(usarIconeBanco = false, bancoId = null)
        }
    }

    fun updateIncluirNoTotal(incluir: Boolean) {
        _formState.update { it.copy(incluirNoTotal = incluir) }
    }

    fun resetForm() {
        val primeiroTipo = _uiState.value.tiposConta.firstOrNull()?.id
        _formState.value = ContaFormState(tipoContaId = primeiroTipo)
    }

    fun loadContaForEdit(contaId: Long) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }
            try {
                val conta = contaRepository.getContaById(contaId)
                if (conta != null) {
                    _formState.update {
                        it.copy(
                            id = conta.id,
                            nome = conta.nome,
                            tipoContaId = conta.tipoContaId,
                            saldoInicial = (conta.saldoInicial * 100).toLong(),
                            cor = conta.cor ?: "#4CAF50",
                            icone = conta.icone,
                            bancoId = conta.bancoId,
                            usarIconeBanco = conta.bancoId != null,
                            incluirNoTotal = conta.incluirNoTotal,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _formState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun saveConta() {
        val form = _formState.value
        if (form.nome.isBlank() || form.tipoContaId == null) {
            _formState.update { it.copy(error = "Preencha os campos obrigatórios") }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }
            try {
                val saldoEmReais = form.saldoInicial / 100.0
                val conta = Conta(
                    id = form.id,
                    nome = form.nome.trim(),
                    tipoContaId = form.tipoContaId,
                    saldoInicial = saldoEmReais,
                    saldoAtual = if (form.id == 0L) saldoEmReais else {
                        contaRepository.getContaById(form.id)?.saldoAtual ?: saldoEmReais
                    },
                    cor = form.cor,
                    icone = form.icone,
                    bancoId = form.bancoId,
                    incluirNoTotal = form.incluirNoTotal
                )
                if (form.id == 0L) contaRepository.insertConta(conta) else contaRepository.updateConta(conta)
                _formState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _formState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteConta(contaId: Long) {
        viewModelScope.launch {
            try { contaRepository.deleteContaById(contaId) } catch (e: Exception) { _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
        _formState.update { it.copy(error = null) }
    }
}

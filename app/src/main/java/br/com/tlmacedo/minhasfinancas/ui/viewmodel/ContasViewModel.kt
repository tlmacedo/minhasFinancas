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

/**
 * Representa o estado da UI para a tela de listagem de contas.
 * 
 * @property contas Lista de contas enriquecidas com seus respectivos tipos.
 * @property tiposConta Lista de tipos de conta disponíveis para seleção.
 * @property saldoTotal Soma do saldo de todas as contas configuradas para exibição.
 * @property isLoading Indica se os dados ainda estão sendo carregados.
 * @property error Mensagem de erro caso ocorra falha na recuperação dos dados.
 */
data class ContasUiState(
    val contas: List<ContaComTipo> = emptyList(),
    val tiposConta: List<TipoConta> = emptyList(),
    val saldoTotal: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * Representa o estado dos dados no formulário de criação/edição de conta.
 */
data class ContaFormState(
    val id: Long = 0,
    val nome: String = "",
    val tipoContaId: Long? = null,
    val saldoInicial: Long = 0, // Armazenado em centavos para precisão no TextField
    val cor: String = "#4CAF50",
    val icone: String? = "account_balance_wallet",
    val bancoId: String? = null,
    val usarIconeBanco: Boolean = false,
    val incluirNoTotal: Boolean = true,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel responsável pela lógica de negócio das Contas Financeiras.
 * 
 * Gerencia o ciclo de vida dos dados de contas, desde a listagem reativa (Flow)
 * até a persistência de novas contas ou atualizações.
 */
@HiltViewModel
class ContasViewModel @Inject constructor(
    private val contaRepository: ContaRepository,
    private val tipoContaRepository: TipoContaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContasUiState())
    /** Estado observável para a tela de lista de contas */
    val uiState: StateFlow<ContasUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(ContaFormState())
    /** Estado observável para o formulário de conta */
    val formState: StateFlow<ContaFormState> = _formState.asStateFlow()

    init {
        loadContas()
        loadTiposConta()
    }

    /** 
     * Inicia a coleta reativa das contas do banco de dados.
     * Atualiza automaticamente a UI e recalcula o saldo total sempre que houver mudança.
     */
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

    /** Carrega os tipos de conta cadastrados para popular seletores na UI */
    private fun loadTiposConta() {
        viewModelScope.launch {
            try {
                tipoContaRepository.getAllTiposConta().collect { tipos ->
                    _uiState.update { it.copy(tiposConta = tipos, isLoading = false) }
                    // Define o primeiro tipo como padrão se nenhum estiver selecionado
                    if (_formState.value.tipoContaId == null && tipos.isNotEmpty()) {
                        _formState.update { it.copy(tipoContaId = tipos.first().id) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // --- Métodos de Atualização de Campos do Formulário ---

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

    /** Alterna entre usar um ícone genérico ou a logo de um banco específico */
    fun setUsarIconeBanco(usar: Boolean) {
        _formState.update { state ->
            if (usar) state.copy(usarIconeBanco = true, icone = null)
            else state.copy(usarIconeBanco = false, bancoId = null)
        }
    }

    fun updateIncluirNoTotal(incluir: Boolean) {
        _formState.update { it.copy(incluirNoTotal = incluir) }
    }

    /** Reseta o formulário para o estado inicial de criação */
    fun resetForm() {
        val primeiroTipo = _uiState.value.tiposConta.firstOrNull()?.id
        _formState.value = ContaFormState(tipoContaId = primeiroTipo)
    }

    /** 
     * Carrega os dados de uma conta específica para edição.
     * @param contaId ID da conta a ser carregada.
     */
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

    /** 
     * Persiste os dados do formulário no banco de dados.
     * Realiza a inserção se for uma nova conta ou atualização se já existir.
     */
    fun saveConta() {
        val form = _formState.value
        // Validação básica
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
                    // Se for edição, mantém o saldo atual. Se for nova, usa o saldo inicial.
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

    /** Exclui uma conta permanentemente pelo ID */
    fun deleteConta(contaId: Long) {
        viewModelScope.launch {
            try { contaRepository.deleteContaById(contaId) } catch (e: Exception) { _uiState.update { it.copy(error = e.message) } }
        }
    }

    /** Limpa os estados de erro da UI */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
        _formState.update { it.copy(error = null) }
    }
}

package br.com.tlmacedo.minhasfinancas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.tlmacedo.minhasfinancas.data.local.dao.EventoCompleto
import br.com.tlmacedo.minhasfinancas.data.local.entity.Categoria
import br.com.tlmacedo.minhasfinancas.data.local.entity.Conta
import br.com.tlmacedo.minhasfinancas.data.local.entity.Evento
import br.com.tlmacedo.minhasfinancas.data.repository.CategoriaRepository
import br.com.tlmacedo.minhasfinancas.data.repository.ContaRepository
import br.com.tlmacedo.minhasfinancas.data.repository.EventoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class EventosUiState(
    val eventos: List<EventoCompleto> = emptyList(),
    val contas: List<Conta> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val mesSelecionado: Calendar = Calendar.getInstance(),
    val receitasMes: Double = 0.0,
    val despesasMes: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null
)

data class EventoFormState(
    val id: Long = 0,
    val descricao: String = "",
    val valor: Long = 0, // em centavos
    val tipo: String = "DESPESA",
    val data: Long = System.currentTimeMillis(),
    val contaId: Long? = null,
    val categoriaId: Long? = null,
    val observacao: String = "",
    val efetivado: Boolean = true,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EventosViewModel @Inject constructor(
    private val eventoRepository: EventoRepository,
    private val contaRepository: ContaRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventosUiState())
    val uiState: StateFlow<EventosUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(EventoFormState())
    val formState: StateFlow<EventoFormState> = _formState.asStateFlow()

    init {
        loadContas()
        loadCategorias()
        loadEventosMesAtual()
    }

    private fun loadContas() {
        viewModelScope.launch {
            contaRepository.getContasAtivas().collect { contas ->
                _uiState.update { it.copy(contas = contas) }
            }
        }
    }

    private fun loadCategorias() {
        viewModelScope.launch {
            categoriaRepository.getAllCategorias().collect { categorias ->
                _uiState.update { it.copy(categorias = categorias) }
            }
        }
    }

    private fun loadEventosMesAtual() {
        val cal = _uiState.value.mesSelecionado
        val (inicio, fim) = getMonthRange(cal)
        loadEventosPeriodo(inicio, fim)
    }

    private fun loadEventosPeriodo(inicio: Long, fim: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            eventoRepository.getEventosCompletosByPeriodo(inicio, fim).collect { eventos ->
                val receitas = eventos
                    .filter { it.evento.tipo == "RECEITA" && it.evento.efetivado }
                    .sumOf { it.evento.valor }
                val despesas = eventos
                    .filter { it.evento.tipo == "DESPESA" && it.evento.efetivado }
                    .sumOf { it.evento.valor }
                
                _uiState.update { 
                    it.copy(
                        eventos = eventos,
                        receitasMes = receitas,
                        despesasMes = despesas,
                        isLoading = false
                    ) 
                }
            }
        }
    }

    fun navegarMes(offset: Int) {
        val novoCal = _uiState.value.mesSelecionado.clone() as Calendar
        novoCal.add(Calendar.MONTH, offset)
        _uiState.update { it.copy(mesSelecionado = novoCal) }
        loadEventosMesAtual()
    }

    // Form updates
    fun updateDescricao(descricao: String) {
        _formState.update { it.copy(descricao = descricao, error = null) }
    }

    fun updateValor(centavos: Long) {
        _formState.update { it.copy(valor = centavos) }
    }

    fun updateTipo(tipo: String) {
        _formState.update { it.copy(tipo = tipo, categoriaId = null) }
    }

    fun updateData(data: Long) {
        _formState.update { it.copy(data = data) }
    }

    fun updateConta(contaId: Long) {
        _formState.update { it.copy(contaId = contaId) }
    }

    fun updateCategoria(categoriaId: Long?) {
        _formState.update { it.copy(categoriaId = categoriaId) }
    }

    fun updateObservacao(observacao: String) {
        _formState.update { it.copy(observacao = observacao) }
    }

    fun updateEfetivado(efetivado: Boolean) {
        _formState.update { it.copy(efetivado = efetivado) }
    }

    fun resetForm() {
        val primeiraConta = _uiState.value.contas.firstOrNull()?.id
        _formState.value = EventoFormState(contaId = primeiraConta)
    }

    fun loadEventoForEdit(eventoId: Long) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }
            
            val evento = eventoRepository.getEventoById(eventoId)
            if (evento != null) {
                _formState.update {
                    it.copy(
                        id = evento.id,
                        descricao = evento.descricao,
                        valor = (evento.valor * 100).toLong(),
                        tipo = evento.tipo,
                        data = evento.data,
                        contaId = evento.contaId,
                        categoriaId = evento.categoriaId,
                        observacao = evento.observacao ?: "",
                        efetivado = evento.efetivado,
                        isLoading = false
                    )
                }
            } else {
                _formState.update { it.copy(error = "Evento não encontrado", isLoading = false) }
            }
        }
    }

    fun saveEvento() {
        val form = _formState.value
        
        // Validações
        if (form.descricao.isBlank()) {
            _formState.update { it.copy(error = "Descrição é obrigatória") }
            return
        }
        
        if (form.valor <= 0) {
            _formState.update { it.copy(error = "Valor deve ser maior que zero") }
            return
        }
        
        if (form.contaId == null) {
            _formState.update { it.copy(error = "Selecione uma conta") }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }
            
            try {
                val valorEmReais = form.valor / 100.0
                
                val evento = Evento(
                    id = form.id,
                    descricao = form.descricao.trim(),
                    valor = valorEmReais,
                    tipo = form.tipo,
                    data = form.data,
                    contaId = form.contaId,
                    categoriaId = form.categoriaId,
                    observacao = form.observacao.ifBlank { null },
                    efetivado = form.efetivado
                )
                
                if (form.id == 0L) {
                    eventoRepository.insertEvento(evento)
                } else {
                    eventoRepository.updateEvento(evento)
                }
                
                // Atualizar saldo da conta se efetivado
                if (form.efetivado) {
                    atualizarSaldoConta(form.contaId, valorEmReais, form.tipo, form.id == 0L)
                }
                
                _formState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _formState.update { 
                    it.copy(isLoading = false, error = e.message ?: "Erro ao salvar evento") 
                }
            }
        }
    }

    private suspend fun atualizarSaldoConta(contaId: Long, valor: Double, tipo: String, isNovo: Boolean) {
        val conta = contaRepository.getContaById(contaId) ?: return
        val novoSaldo = if (tipo == "RECEITA") {
            conta.saldoAtual + valor
        } else {
            conta.saldoAtual - valor
        }
        contaRepository.updateSaldo(contaId, novoSaldo)
    }

    fun deleteEvento(eventoId: Long) {
        viewModelScope.launch {
            try {
                eventoRepository.deleteEventoById(eventoId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
        _formState.update { it.copy(error = null) }
    }

    private fun getMonthRange(cal: Calendar): Pair<Long, Long> {
        val inicio = cal.clone() as Calendar
        inicio.set(Calendar.DAY_OF_MONTH, 1)
        inicio.set(Calendar.HOUR_OF_DAY, 0)
        inicio.set(Calendar.MINUTE, 0)
        inicio.set(Calendar.SECOND, 0)
        inicio.set(Calendar.MILLISECOND, 0)
        
        val fim = cal.clone() as Calendar
        fim.set(Calendar.DAY_OF_MONTH, fim.getActualMaximum(Calendar.DAY_OF_MONTH))
        fim.set(Calendar.HOUR_OF_DAY, 23)
        fim.set(Calendar.MINUTE, 59)
        fim.set(Calendar.SECOND, 59)
        fim.set(Calendar.MILLISECOND, 999)
        
        return Pair(inicio.timeInMillis, fim.timeInMillis)
    }
}

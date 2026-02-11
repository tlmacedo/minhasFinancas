package br.com.tlmacedo.minhasfinancas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.tlmacedo.minhasfinancas.data.local.dao.ContaComTipo
import br.com.tlmacedo.minhasfinancas.data.local.dao.EventoCompleto
import br.com.tlmacedo.minhasfinancas.data.repository.ContaRepository
import br.com.tlmacedo.minhasfinancas.data.repository.EventoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class DashboardUiState(
    val saldoTotal: Double = 0.0,
    val receitasMes: Double = 0.0,
    val despesasMes: Double = 0.0,
    val contas: List<ContaComTipo> = emptyList(),
    val ultimosEventos: List<EventoCompleto> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val contaRepository: ContaRepository,
    private val eventoRepository: EventoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        loadContas()
        loadResumoMes()
        loadUltimosEventos()
    }

    private fun loadContas() {
        viewModelScope.launch {
            contaRepository.getAllContasComTipo().collect { contas ->
                val saldoTotal = contas
                    .filter { it.conta.incluirNoTotal && it.conta.ativa }
                    .sumOf { it.conta.saldoAtual }
                
                _uiState.update { 
                    it.copy(
                        contas = contas,
                        saldoTotal = saldoTotal,
                        isLoading = false
                    ) 
                }
            }
        }
    }

    private fun loadResumoMes() {
        val (inicio, fim) = getMonthRange()
        
        viewModelScope.launch {
            // Receitas do mês
            eventoRepository.getReceitasPeriodo(inicio, fim).collect { receitas ->
                _uiState.update { it.copy(receitasMes = receitas) }
            }
        }
        
        viewModelScope.launch {
            // Despesas do mês
            eventoRepository.getDespesasPeriodo(inicio, fim).collect { despesas ->
                _uiState.update { it.copy(despesasMes = despesas) }
            }
        }
    }

    private fun loadUltimosEventos() {
        viewModelScope.launch {
            eventoRepository.getAllEventosCompletos()
                .map { eventos -> eventos.take(5) } // Pegar apenas os 5 últimos
                .collect { eventos ->
                    _uiState.update { it.copy(ultimosEventos = eventos) }
                }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadDashboardData()
    }

    private fun getMonthRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        
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

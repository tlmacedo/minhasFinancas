package br.com.tlmacedo.minhasfinancas.data.repository

import br.com.tlmacedo.minhasfinancas.data.local.dao.EventoCompleto
import br.com.tlmacedo.minhasfinancas.data.local.dao.EventoDao
import br.com.tlmacedo.minhasfinancas.data.local.entity.Evento
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventoRepository @Inject constructor(
    private val eventoDao: EventoDao
) {
    fun getAllEventos(): Flow<List<Evento>> = eventoDao.getAllEventos()
    
    fun getAllEventosCompletos(): Flow<List<EventoCompleto>> = eventoDao.getAllEventosCompletos()
    
    fun getEventosByContaId(contaId: Long): Flow<List<EventoCompleto>> = 
        eventoDao.getEventosByContaId(contaId)
    
    fun getEventosByPeriodo(inicio: Long, fim: Long): Flow<List<Evento>> = 
        eventoDao.getEventosByPeriodo(inicio, fim)
    
    fun getEventosCompletosByPeriodo(inicio: Long, fim: Long): Flow<List<EventoCompleto>> = 
        eventoDao.getEventosCompletosByPeriodo(inicio, fim)
    
    suspend fun getEventoById(id: Long): Evento? = eventoDao.getEventoById(id)
    
    fun getReceitasPeriodo(inicio: Long, fim: Long): Flow<Double> = 
        eventoDao.getReceitasPeriodo(inicio, fim)
    
    fun getDespesasPeriodo(inicio: Long, fim: Long): Flow<Double> = 
        eventoDao.getDespesasPeriodo(inicio, fim)
    
    suspend fun insertEvento(evento: Evento): Long = eventoDao.insert(evento)
    
    suspend fun updateEvento(evento: Evento) = eventoDao.update(evento)
    
    suspend fun deleteEvento(evento: Evento) = eventoDao.delete(evento)
    
    suspend fun deleteEventoById(id: Long) = eventoDao.deleteById(id)
}

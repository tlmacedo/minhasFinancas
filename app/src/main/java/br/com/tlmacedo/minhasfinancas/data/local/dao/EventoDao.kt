package br.com.tlmacedo.minhasfinancas.data.local.dao

import androidx.room.*
import br.com.tlmacedo.minhasfinancas.data.local.entity.Categoria
import br.com.tlmacedo.minhasfinancas.data.local.entity.Conta
import br.com.tlmacedo.minhasfinancas.data.local.entity.Evento
import kotlinx.coroutines.flow.Flow

data class EventoCompleto(
    @Embedded val evento: Evento,
    @Relation(
        parentColumn = "contaId",
        entityColumn = "id"
    )
    val conta: Conta,
    @Relation(
        parentColumn = "categoriaId",
        entityColumn = "id"
    )
    val categoria: Categoria?
)

@Dao
interface EventoDao {
    @Query("SELECT * FROM eventos ORDER BY data DESC")
    fun getAllEventos(): Flow<List<Evento>>
    
    @Transaction
    @Query("SELECT * FROM eventos ORDER BY data DESC")
    fun getAllEventosCompletos(): Flow<List<EventoCompleto>>
    
    @Transaction
    @Query("SELECT * FROM eventos WHERE contaId = :contaId ORDER BY data DESC")
    fun getEventosByContaId(contaId: Long): Flow<List<EventoCompleto>>
    
    @Query("SELECT * FROM eventos WHERE data BETWEEN :inicio AND :fim ORDER BY data DESC")
    fun getEventosByPeriodo(inicio: Long, fim: Long): Flow<List<Evento>>
    
    @Transaction
    @Query("SELECT * FROM eventos WHERE data BETWEEN :inicio AND :fim ORDER BY data DESC")
    fun getEventosCompletosByPeriodo(inicio: Long, fim: Long): Flow<List<EventoCompleto>>
    
    @Query("SELECT * FROM eventos WHERE id = :id")
    suspend fun getEventoById(id: Long): Evento?
    
    @Query("""
        SELECT COALESCE(SUM(valor), 0) FROM eventos 
        WHERE tipo = 'RECEITA' AND data BETWEEN :inicio AND :fim AND efetivado = 1
    """)
    fun getReceitasPeriodo(inicio: Long, fim: Long): Flow<Double>
    
    @Query("""
        SELECT COALESCE(SUM(valor), 0) FROM eventos 
        WHERE tipo = 'DESPESA' AND data BETWEEN :inicio AND :fim AND efetivado = 1
    """)
    fun getDespesasPeriodo(inicio: Long, fim: Long): Flow<Double>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(evento: Evento): Long
    
    @Update
    suspend fun update(evento: Evento)
    
    @Delete
    suspend fun delete(evento: Evento)
    
    @Query("DELETE FROM eventos WHERE id = :id")
    suspend fun deleteById(id: Long)
}

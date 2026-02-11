package br.com.tlmacedo.minhasfinancas.data.local.dao

import androidx.room.*
import br.com.tlmacedo.minhasfinancas.data.local.entity.TipoConta
import kotlinx.coroutines.flow.Flow

@Dao
interface TipoContaDao {
    @Query("SELECT * FROM tipos_conta ORDER BY nome")
    fun getAllTiposConta(): Flow<List<TipoConta>>
    
    @Query("SELECT * FROM tipos_conta WHERE id = :id")
    suspend fun getTipoContaById(id: Long): TipoConta?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tipoConta: TipoConta): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tiposConta: List<TipoConta>)
    
    @Update
    suspend fun update(tipoConta: TipoConta)
    
    @Delete
    suspend fun delete(tipoConta: TipoConta)
}

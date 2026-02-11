package br.com.tlmacedo.minhasfinancas.data.local.dao

import androidx.room.*
import br.com.tlmacedo.minhasfinancas.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias WHERE ativa = 1 ORDER BY nome")
    fun getAllCategorias(): Flow<List<Categoria>>
    
    @Query("SELECT * FROM categorias WHERE tipo = :tipo AND ativa = 1 ORDER BY nome")
    fun getCategoriasByTipo(tipo: String): Flow<List<Categoria>>
    
    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun getCategoriaById(id: Long): Categoria?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoria: Categoria): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categorias: List<Categoria>)
    
    @Update
    suspend fun update(categoria: Categoria)
    
    @Delete
    suspend fun delete(categoria: Categoria)
}

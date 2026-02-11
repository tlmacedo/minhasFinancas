package br.com.tlmacedo.minhasfinancas.data.local.dao

import androidx.room.*
import br.com.tlmacedo.minhasfinancas.data.local.entity.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE ativo = 1 ORDER BY nome")
    fun getAllUsuarios(): Flow<List<Usuario>>
    
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun getUsuarioById(id: Long): Usuario?
    
    @Query("SELECT * FROM usuarios WHERE email = :email AND ativo = 1")
    suspend fun getUsuarioByEmail(email: String): Usuario?
    
    @Query("SELECT COUNT(*) FROM usuarios WHERE ativo = 1")
    suspend fun countUsuarios(): Int
    
    @Query("SELECT COUNT(*) FROM usuarios WHERE ativo = 1")
    fun countUsuariosFlow(): Flow<Int>
    
    @Query("UPDATE usuarios SET ultimoAcesso = :timestamp WHERE id = :id")
    suspend fun updateUltimoAcesso(id: Long, timestamp: Long)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: Usuario): Long
    
    @Update
    suspend fun update(usuario: Usuario)
    
    @Delete
    suspend fun delete(usuario: Usuario)
    
    @Query("UPDATE usuarios SET ativo = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)
}

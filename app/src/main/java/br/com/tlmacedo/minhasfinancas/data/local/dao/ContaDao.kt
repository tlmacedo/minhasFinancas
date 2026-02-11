package br.com.tlmacedo.minhasfinancas.data.local.dao

import androidx.room.*
import br.com.tlmacedo.minhasfinancas.data.local.entity.Conta
import br.com.tlmacedo.minhasfinancas.data.local.entity.TipoConta
import kotlinx.coroutines.flow.Flow

data class ContaComTipo(
    @Embedded val conta: Conta,
    @Relation(
        parentColumn = "tipoContaId",
        entityColumn = "id"
    )
    val tipoConta: TipoConta
)

@Dao
interface ContaDao {
    @Query("SELECT * FROM contas WHERE ativa = 1 ORDER BY nome")
    fun getAllContas(): Flow<List<Conta>>
    
    @Transaction
    @Query("SELECT * FROM contas WHERE ativa = 1 ORDER BY nome")
    fun getAllContasComTipo(): Flow<List<ContaComTipo>>
    
    @Query("SELECT * FROM contas WHERE ativa = 1 ORDER BY nome")
    fun getContasAtivas(): Flow<List<Conta>>
    
    @Query("SELECT * FROM contas WHERE id = :id")
    suspend fun getContaById(id: Long): Conta?
    
    @Query("SELECT SUM(saldoAtual) FROM contas WHERE incluirNoTotal = 1 AND ativa = 1")
    fun getSaldoTotal(): Flow<Double?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conta: Conta): Long
    
    @Update
    suspend fun update(conta: Conta)
    
    @Delete
    suspend fun delete(conta: Conta)
    
    @Query("DELETE FROM contas WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("UPDATE contas SET saldoAtual = :novoSaldo WHERE id = :contaId")
    suspend fun updateSaldo(contaId: Long, novoSaldo: Double)
}

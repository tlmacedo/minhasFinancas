package br.com.tlmacedo.minhasfinancas.data.repository

import br.com.tlmacedo.minhasfinancas.data.local.dao.ContaComTipo
import br.com.tlmacedo.minhasfinancas.data.local.dao.ContaDao
import br.com.tlmacedo.minhasfinancas.data.local.entity.Conta
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContaRepository @Inject constructor(
    private val contaDao: ContaDao
) {
    fun getAllContas(): Flow<List<Conta>> = contaDao.getAllContas()
    
    fun getAllContasComTipo(): Flow<List<ContaComTipo>> = contaDao.getAllContasComTipo()
    
    fun getContasAtivas(): Flow<List<Conta>> = contaDao.getContasAtivas()
    
    suspend fun getContaById(id: Long): Conta? = contaDao.getContaById(id)
    
    suspend fun insertConta(conta: Conta): Long = contaDao.insert(conta)
    
    suspend fun updateConta(conta: Conta) = contaDao.update(conta)
    
    suspend fun deleteConta(conta: Conta) = contaDao.delete(conta)
    
    suspend fun deleteContaById(id: Long) = contaDao.deleteById(id)
    
    suspend fun updateSaldo(contaId: Long, novoSaldo: Double) = 
        contaDao.updateSaldo(contaId, novoSaldo)
    
    fun getSaldoTotal(): Flow<Double?> = contaDao.getSaldoTotal()
}

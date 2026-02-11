package br.com.tlmacedo.minhasfinancas.data.repository

import br.com.tlmacedo.minhasfinancas.data.local.dao.TipoContaDao
import br.com.tlmacedo.minhasfinancas.data.local.entity.TipoConta
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TipoContaRepository @Inject constructor(
    private val tipoContaDao: TipoContaDao
) {
    fun getAllTiposConta(): Flow<List<TipoConta>> = tipoContaDao.getAllTiposConta()
    
    suspend fun getTipoContaById(id: Long): TipoConta? = tipoContaDao.getTipoContaById(id)
    
    suspend fun insertTipoConta(tipoConta: TipoConta): Long = tipoContaDao.insert(tipoConta)
    
    suspend fun updateTipoConta(tipoConta: TipoConta) = tipoContaDao.update(tipoConta)
    
    suspend fun deleteTipoConta(tipoConta: TipoConta) = tipoContaDao.delete(tipoConta)
}

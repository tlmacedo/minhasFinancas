package br.com.tlmacedo.minhasfinancas.data.repository

import br.com.tlmacedo.minhasfinancas.data.local.dao.ContaComTipo
import br.com.tlmacedo.minhasfinancas.data.local.dao.ContaDao
import br.com.tlmacedo.minhasfinancas.data.local.entity.Conta
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositório responsável pela gestão dos dados de Contas.
 * 
 * Atua como uma camada de abstração sobre o [ContaDao], fornecendo fluxos de dados (Flow)
 * e funções suspensas para manipulação das contas financeiras.
 * É injetado como um Singleton para garantir consistência em toda a aplicação.
 */
@Singleton
class ContaRepository @Inject constructor(
    private val contaDao: ContaDao
) {
    /** Retorna um fluxo de todas as contas cadastradas ordenadas por nome */
    fun getAllContas(): Flow<List<Conta>> = contaDao.getAllContas()
    
    /** Retorna um fluxo de todas as contas incluindo os detalhes de seu [TipoConta] */
    fun getAllContasComTipo(): Flow<List<ContaComTipo>> = contaDao.getAllContasComTipo()
    
    /** Retorna apenas as contas que estão marcadas como ativas */
    fun getContasAtivas(): Flow<List<Conta>> = contaDao.getContasAtivas()
    
    /** Busca uma conta específica pelo seu identificador único */
    suspend fun getContaById(id: Long): Conta? = contaDao.getContaById(id)
    
    /** Insere uma nova conta no banco de dados e retorna seu ID */
    suspend fun insertConta(conta: Conta): Long = contaDao.insert(conta)
    
    /** Atualiza as informações de uma conta existente */
    suspend fun updateConta(conta: Conta) = contaDao.update(conta)
    
    /** Remove uma conta do banco de dados */
    suspend fun deleteConta(conta: Conta) = contaDao.delete(conta)
    
    /** Remove uma conta do banco de dados utilizando apenas o seu ID */
    suspend fun deleteContaById(id: Long) = contaDao.deleteById(id)
    
    /** 
     * Atualiza o saldo atual de uma conta específica.
     * Geralmente chamado após o processamento de novas transações.
     */
    suspend fun updateSaldo(contaId: Long, novoSaldo: Double) = 
        contaDao.updateSaldo(contaId, novoSaldo)
    
    /** Retorna a soma do saldo de todas as contas marcadas para 'incluir no total' */
    fun getSaldoTotal(): Flow<Double?> = contaDao.getSaldoTotal()
}

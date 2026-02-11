package br.com.tlmacedo.minhasfinancas.data.repository

import br.com.tlmacedo.minhasfinancas.data.local.dao.CategoriaDao
import br.com.tlmacedo.minhasfinancas.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriaRepository @Inject constructor(
    private val categoriaDao: CategoriaDao
) {
    fun getAllCategorias(): Flow<List<Categoria>> = categoriaDao.getAllCategorias()
    
    fun getCategoriasByTipo(tipo: String): Flow<List<Categoria>> = 
        categoriaDao.getCategoriasByTipo(tipo)
    
    fun getCategoriasReceita(): Flow<List<Categoria>> = 
        categoriaDao.getCategoriasByTipo("RECEITA")
    
    fun getCategoriasDespesa(): Flow<List<Categoria>> = 
        categoriaDao.getCategoriasByTipo("DESPESA")
    
    suspend fun getCategoriaById(id: Long): Categoria? = categoriaDao.getCategoriaById(id)
    
    suspend fun insertCategoria(categoria: Categoria): Long = categoriaDao.insert(categoria)
    
    suspend fun updateCategoria(categoria: Categoria) = categoriaDao.update(categoria)
    
    suspend fun deleteCategoria(categoria: Categoria) = categoriaDao.delete(categoria)
}

package br.com.tlmacedo.minhasfinancas.data.repository

import br.com.tlmacedo.minhasfinancas.data.local.dao.UsuarioDao
import br.com.tlmacedo.minhasfinancas.data.local.entity.Usuario
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepository @Inject constructor(
    private val usuarioDao: UsuarioDao
) {
    fun getAllUsuarios(): Flow<List<Usuario>> = usuarioDao.getAllUsuarios()
    
    suspend fun getUsuarioById(id: Long): Usuario? = usuarioDao.getUsuarioById(id)
    
    suspend fun getUsuarioByEmail(email: String): Usuario? = usuarioDao.getUsuarioByEmail(email)
    
    suspend fun countUsuarios(): Int = usuarioDao.countUsuarios()
    
    fun countUsuariosFlow(): Flow<Int> = usuarioDao.countUsuariosFlow()
    
    suspend fun insertUsuario(usuario: Usuario): Long = usuarioDao.insert(usuario)
    
    suspend fun updateUsuario(usuario: Usuario) = usuarioDao.update(usuario)
    
    suspend fun deactivateUsuario(id: Long) = usuarioDao.deactivate(id)
    
    suspend fun updateUltimoAcesso(id: Long) = 
        usuarioDao.updateUltimoAcesso(id, System.currentTimeMillis())
    
    suspend fun autenticarPorSenha(email: String, senha: String): Usuario? {
        val usuario = usuarioDao.getUsuarioByEmail(email) ?: return null
        val senhaHash = hashSenha(senha)
        return if (usuario.senhaHash == senhaHash) usuario else null
    }
    
    fun hashSenha(senha: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(senha.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

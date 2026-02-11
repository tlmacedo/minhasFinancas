package br.com.tlmacedo.minhasfinancas.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import br.com.tlmacedo.minhasfinancas.data.local.entity.Usuario
import br.com.tlmacedo.minhasfinancas.data.repository.UsuarioRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object NeedSetup : AuthState() // Nenhum usuário cadastrado
    object NeedLogin : AuthState()
    data class Authenticated(val usuario: Usuario) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class BiometricStatus {
    object Available : BiometricStatus()
    object NotAvailable : BiometricStatus()
    object NotEnrolled : BiometricStatus()
    object HardwareNotPresent : BiometricStatus()
}

@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val usuarioRepository: UsuarioRepository
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()
    
    suspend fun checkInitialState() {
        _authState.value = AuthState.Loading
        val count = usuarioRepository.countUsuarios()
        _authState.value = if (count == 0) {
            AuthState.NeedSetup
        } else {
            AuthState.NeedLogin
        }
    }
    
    suspend fun loginWithPassword(email: String, senha: String): Result<Usuario> {
        _authState.value = AuthState.Loading
        
        return try {
            val usuario = usuarioRepository.autenticarPorSenha(email, senha)
            if (usuario != null) {
                usuarioRepository.updateUltimoAcesso(usuario.id)
                _currentUser.value = usuario
                _authState.value = AuthState.Authenticated(usuario)
                Result.success(usuario)
            } else {
                _authState.value = AuthState.Error("Email ou senha inválidos")
                Result.failure(Exception("Email ou senha inválidos"))
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Erro ao autenticar")
            Result.failure(e)
        }
    }
    
    fun checkBiometricStatus(): BiometricStatus {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.HardwareNotPresent
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.NotAvailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NotEnrolled
            else -> BiometricStatus.NotAvailable
        }
    }
    
    suspend fun loginWithBiometric(usuarioId: Long): Result<Usuario> {
        return try {
            val usuario = usuarioRepository.getUsuarioById(usuarioId)
            if (usuario != null && usuario.usarBiometria) {
                usuarioRepository.updateUltimoAcesso(usuario.id)
                _currentUser.value = usuario
                _authState.value = AuthState.Authenticated(usuario)
                Result.success(usuario)
            } else {
                Result.failure(Exception("Biometria não habilitada para este usuário"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createUser(
        nome: String,
        email: String,
        senha: String,
        usarBiometria: Boolean = false,
        fotoUri: String? = null,
        isFirstUser: Boolean = false
    ): Result<Usuario> {
        return try {
            // Verificar se email já existe
            val existingUser = usuarioRepository.getUsuarioByEmail(email)
            if (existingUser != null) {
                return Result.failure(Exception("Email já cadastrado"))
            }
            
            val usuario = Usuario(
                nome = nome,
                email = email,
                senhaHash = usuarioRepository.hashSenha(senha),
                usarBiometria = usarBiometria,
                fotoPerfilUri = fotoUri,
                isAdmin = isFirstUser
            )
            
            val id = usuarioRepository.insertUsuario(usuario)
            val savedUser = usuario.copy(id = id)
            
            Result.success(savedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.NeedLogin
    }
    
    fun isLoggedIn(): Boolean = _currentUser.value != null
    
    fun canCreateUsers(): Boolean {
        return _currentUser.value?.isAdmin == true || _authState.value == AuthState.NeedSetup
    }
}

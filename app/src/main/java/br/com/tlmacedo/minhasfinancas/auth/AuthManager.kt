package br.com.tlmacedo.minhasfinancas.auth

import android.content.Context
import android.net.Uri
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import br.com.tlmacedo.minhasfinancas.data.local.entity.Usuario
import br.com.tlmacedo.minhasfinancas.data.repository.UsuarioRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object NeedSetup : AuthState()
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
        _authState.value = if (count == 0) AuthState.NeedSetup else AuthState.NeedLogin
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
                Result.failure(Exception("Biometria não habilitada"))
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
    ): Result<Usuario> = withContext(Dispatchers.IO) {
        try {
            val existingUser = usuarioRepository.getUsuarioByEmail(email)
            if (existingUser != null) return@withContext Result.failure(Exception("Email já cadastrado"))
            
            // Persistir imagem se houver
            val finalFotoUri = fotoUri?.let { uriString ->
                saveImageToInternalStorage(Uri.parse(uriString))
            }
            
            val usuario = Usuario(
                nome = nome,
                email = email,
                senhaHash = usuarioRepository.hashSenha(senha),
                usarBiometria = usarBiometria,
                fotoPerfilUri = finalFotoUri,
                isAdmin = isFirstUser
            )
            
            val id = usuarioRepository.insertUsuario(usuario)
            Result.success(usuario.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.NeedLogin
    }
    
    fun isLoggedIn(): Boolean = _currentUser.value != null
    fun canCreateUsers(): Boolean = _currentUser.value?.isAdmin == true || _authState.value == AuthState.NeedSetup
}

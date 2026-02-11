package br.com.tlmacedo.minhasfinancas.ui.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.tlmacedo.minhasfinancas.auth.AuthManager
import br.com.tlmacedo.minhasfinancas.auth.AuthState
import br.com.tlmacedo.minhasfinancas.auth.BiometricStatus
import br.com.tlmacedo.minhasfinancas.data.local.entity.Usuario
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")
private val LAST_USER_ID_KEY = longPreferencesKey("last_user_id")

data class LoginFormState(
    val email: String = "",
    val senha: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class RegisterFormState(
    val nome: String = "",
    val email: String = "",
    val senha: String = "",
    val confirmarSenha: String = "",
    val usarBiometria: Boolean = false,
    val fotoUri: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authManager: AuthManager
) : ViewModel() {

    val authState: StateFlow<AuthState> = authManager.authState
    val currentUser: StateFlow<Usuario?> = authManager.currentUser
    
    private val _loginForm = MutableStateFlow(LoginFormState())
    val loginForm: StateFlow<LoginFormState> = _loginForm.asStateFlow()
    
    private val _registerForm = MutableStateFlow(RegisterFormState())
    val registerForm: StateFlow<RegisterFormState> = _registerForm.asStateFlow()
    
    private val _biometricStatus = MutableStateFlow<BiometricStatus>(BiometricStatus.NotAvailable)
    val biometricStatus: StateFlow<BiometricStatus> = _biometricStatus.asStateFlow()
    
    private val _lastLoggedUserId = MutableStateFlow<Long?>(null)
    val lastLoggedUserId: StateFlow<Long?> = _lastLoggedUserId.asStateFlow()

    init {
        checkInitialState()
        checkBiometricStatus()
        loadLastLoggedUserId()
    }

    private fun checkInitialState() {
        viewModelScope.launch {
            authManager.checkInitialState()
        }
    }

    private fun checkBiometricStatus() {
        _biometricStatus.value = authManager.checkBiometricStatus()
    }
    
    private fun loadLastLoggedUserId() {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                _lastLoggedUserId.value = prefs[LAST_USER_ID_KEY]
            }
        }
    }
    
    private suspend fun saveLastLoggedUserId(userId: Long) {
        context.dataStore.edit { prefs ->
            prefs[LAST_USER_ID_KEY] = userId
        }
    }

    // Login Form
    fun updateLoginEmail(email: String) {
        _loginForm.update { it.copy(email = email, error = null) }
    }

    fun updateLoginSenha(senha: String) {
        _loginForm.update { it.copy(senha = senha, error = null) }
    }

    fun loginWithPassword() {
        val form = _loginForm.value
        
        if (form.email.isBlank()) {
            _loginForm.update { it.copy(error = "Email é obrigatório") }
            return
        }
        
        if (form.senha.isBlank()) {
            _loginForm.update { it.copy(error = "Senha é obrigatória") }
            return
        }

        viewModelScope.launch {
            _loginForm.update { it.copy(isLoading = true, error = null) }
            
            val result = authManager.loginWithPassword(form.email, form.senha)
            
            result.fold(
                onSuccess = { usuario ->
                    saveLastLoggedUserId(usuario.id)
                    _lastLoggedUserId.value = usuario.id
                    _loginForm.update { it.copy(isLoading = false) }
                },
                onFailure = { error ->
                    _loginForm.update { 
                        it.copy(isLoading = false, error = error.message) 
                    }
                }
            )
        }
    }

    fun loginWithBiometric(usuarioId: Long) {
        viewModelScope.launch {
            _loginForm.update { it.copy(isLoading = true) }
            
            val result = authManager.loginWithBiometric(usuarioId)
            
            result.fold(
                onSuccess = {
                    _loginForm.update { it.copy(isLoading = false) }
                },
                onFailure = { error ->
                    _loginForm.update { 
                        it.copy(isLoading = false, error = error.message) 
                    }
                }
            )
        }
    }

    fun getAuthManager(): AuthManager = authManager

    // Register Form
    fun updateRegisterNome(nome: String) {
        _registerForm.update { it.copy(nome = nome, error = null) }
    }

    fun updateRegisterEmail(email: String) {
        _registerForm.update { it.copy(email = email, error = null) }
    }

    fun updateRegisterSenha(senha: String) {
        _registerForm.update { it.copy(senha = senha, error = null) }
    }

    fun updateRegisterConfirmarSenha(confirmarSenha: String) {
        _registerForm.update { it.copy(confirmarSenha = confirmarSenha, error = null) }
    }

    fun updateRegisterUsarBiometria(usar: Boolean) {
        _registerForm.update { it.copy(usarBiometria = usar) }
    }
    
    fun updateRegisterFotoUri(uri: String?) {
        _registerForm.update { it.copy(fotoUri = uri) }
    }

    fun register() {
        val form = _registerForm.value
        
        // Validações
        if (form.nome.isBlank()) {
            _registerForm.update { it.copy(error = "Nome é obrigatório") }
            return
        }
        
        if (form.email.isBlank()) {
            _registerForm.update { it.copy(error = "Email é obrigatório") }
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(form.email).matches()) {
            _registerForm.update { it.copy(error = "Email inválido") }
            return
        }
        
        if (form.senha.length < 6) {
            _registerForm.update { it.copy(error = "Senha deve ter pelo menos 6 caracteres") }
            return
        }
        
        if (form.senha != form.confirmarSenha) {
            _registerForm.update { it.copy(error = "Senhas não conferem") }
            return
        }

        viewModelScope.launch {
            _registerForm.update { it.copy(isLoading = true, error = null) }
            
            val isFirstUser = authState.value == AuthState.NeedSetup
            
            val result = authManager.createUser(
                nome = form.nome,
                email = form.email,
                senha = form.senha,
                usarBiometria = form.usarBiometria,
                fotoUri = form.fotoUri,
                isFirstUser = isFirstUser
            )
            
            result.fold(
                onSuccess = { usuario ->
                    saveLastLoggedUserId(usuario.id)
                    _registerForm.update { it.copy(isLoading = false, isSuccess = true) }
                    // Auto-login após registro
                    authManager.loginWithPassword(form.email, form.senha)
                },
                onFailure = { error ->
                    _registerForm.update { 
                        it.copy(isLoading = false, error = error.message) 
                    }
                }
            )
        }
    }

    fun resetRegisterForm() {
        _registerForm.value = RegisterFormState()
    }

    fun resetLoginForm() {
        _loginForm.value = LoginFormState()
    }

    fun logout() {
        authManager.logout()
        resetLoginForm()
    }

    fun canCreateUsers(): Boolean = authManager.canCreateUsers()
    
    fun clearLoginError() {
        _loginForm.update { it.copy(error = null) }
    }
    
    fun clearRegisterError() {
        _registerForm.update { it.copy(error = null) }
    }
}

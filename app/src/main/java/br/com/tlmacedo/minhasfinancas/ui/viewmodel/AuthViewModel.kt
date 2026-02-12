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

// Configuração do DataStore para persistir preferências de autenticação
private val Context.dataStore by preferencesDataStore(name = "auth_prefs")
private val LAST_USER_ID_KEY = longPreferencesKey("last_user_id")

/**
 * Estado que representa os dados do formulário de Login.
 */
data class LoginFormState(
    val email: String = "",
    val senha: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Estado que representa os dados do formulário de Registro de Usuário.
 */
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

/**
 * ViewModel responsável pela lógica de autenticação e gestão de usuários.
 * 
 * Atua como intermediário entre a UI e o [AuthManager], controlando os estados
 * de login, registro e a verificação de biometria.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authManager: AuthManager
) : ViewModel() {

    /** Observa o estado global de autenticação (Autenticado, Precisa de Login, etc) */
    val authState: StateFlow<AuthState> = authManager.authState
    
    /** Observa os dados do usuário atualmente logado */
    val currentUser: StateFlow<Usuario?> = authManager.currentUser
    
    private val _loginForm = MutableStateFlow(LoginFormState())
    /** Estado público do formulário de login */
    val loginForm: StateFlow<LoginFormState> = _loginForm.asStateFlow()
    
    private val _registerForm = MutableStateFlow(RegisterFormState())
    /** Estado público do formulário de registro */
    val registerForm: StateFlow<RegisterFormState> = _registerForm.asStateFlow()
    
    private val _biometricStatus = MutableStateFlow<BiometricStatus>(BiometricStatus.NotAvailable)
    /** Status da disponibilidade de hardware biométrico no dispositivo */
    val biometricStatus: StateFlow<BiometricStatus> = _biometricStatus.asStateFlow()
    
    private val _lastLoggedUserId = MutableStateFlow<Long?>(null)
    /** ID do último usuário que realizou login com sucesso, usado para facilitar login biométrico */
    val lastLoggedUserId: StateFlow<Long?> = _lastLoggedUserId.asStateFlow()

    init {
        checkInitialState()
        checkBiometricStatus()
        loadLastLoggedUserId()
    }

    /** Verifica o estado inicial da aplicação (se há usuários cadastrados ou sessão ativa) */
    private fun checkInitialState() {
        viewModelScope.launch {
            authManager.checkInitialState()
        }
    }

    /** Consulta o hardware do sistema para verificar suporte a biometria */
    private fun checkBiometricStatus() {
        _biometricStatus.value = authManager.checkBiometricStatus()
    }
    
    /** Carrega o ID do último usuário do armazenamento persistente (DataStore) */
    private fun loadLastLoggedUserId() {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                _lastLoggedUserId.value = prefs[LAST_USER_ID_KEY]
            }
        }
    }
    
    /** Salva o ID do usuário após um login bem-sucedido */
    private suspend fun saveLastLoggedUserId(userId: Long) {
        context.dataStore.edit { prefs ->
            prefs[LAST_USER_ID_KEY] = userId
        }
    }

    // --- Métodos de Atualização do Formulário de Login ---

    fun updateLoginEmail(email: String) {
        _loginForm.update { it.copy(email = email, error = null) }
    }

    fun updateLoginSenha(senha: String) {
        _loginForm.update { it.copy(senha = senha, error = null) }
    }

    /** 
     * Executa o processo de login utilizando e-mail e senha.
     * Valida campos obrigatórios antes de chamar o [AuthManager].
     */
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

    /** Realiza o login utilizando a autenticação biométrica do sistema */
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

    /** Retorna a instância do [AuthManager] para uso em diálogos de autenticação na UI */
    fun getAuthManager(): AuthManager = authManager

    // --- Métodos de Atualização do Formulário de Registro ---

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

    /**
     * Realiza o cadastro de um novo usuário.
     * Inclui validações de formato de e-mail, força de senha e confirmação de senha.
     */
    fun register() {
        val form = _registerForm.value
        
        // Validações de entrada
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
                    // Realiza o login automático após o registro bem-sucedido
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

    /** Reinicia o estado do formulário de registro */
    fun resetRegisterForm() {
        _registerForm.value = RegisterFormState()
    }

    /** Reinicia o estado do formulário de login */
    fun resetLoginForm() {
        _loginForm.value = LoginFormState()
    }

    /** Encerra a sessão do usuário atual */
    fun logout() {
        authManager.logout()
        resetLoginForm()
    }

    /** Verifica se a política atual permite a criação de novos usuários */
    fun canCreateUsers(): Boolean = authManager.canCreateUsers()
    
    /** Limpa mensagens de erro de login da UI */
    fun clearLoginError() {
        _loginForm.update { it.copy(error = null) }
    }
    
    /** Limpa mensagens de erro de registro da UI */
    fun clearRegisterError() {
        _registerForm.update { it.copy(error = null) }
    }
}

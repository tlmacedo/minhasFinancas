package br.com.tlmacedo.minhasfinancas.ui.screens.auth

import android.app.Activity
import android.view.autofill.AutofillManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.tlmacedo.minhasfinancas.R
import br.com.tlmacedo.minhasfinancas.auth.AuthState
import br.com.tlmacedo.minhasfinancas.auth.BiometricStatus
import br.com.tlmacedo.minhasfinancas.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val loginForm by viewModel.loginForm.collectAsState()
    val biometricStatus by viewModel.biometricStatus.collectAsState()
    val lastLoggedUserId by viewModel.lastLoggedUserId.collectAsState()
    val authState by viewModel.authState.collectAsState()
    
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val autofill = LocalAutofill.current
    val autofillTree = LocalAutofillTree.current
    
    var showPassword by remember { mutableStateOf(false) }
    
    val isBiometricAvailable = biometricStatus == BiometricStatus.Available
    
    // Nós de autofill
    val emailAutofillNode = remember {
        AutofillNode(
            autofillTypes = listOf(AutofillType.EmailAddress),
            onFill = { viewModel.updateLoginEmail(it) }
        )
    }
    
    val passwordAutofillNode = remember {
        AutofillNode(
            autofillTypes = listOf(AutofillType.Password),
            onFill = { viewModel.updateLoginSenha(it) }
        )
    }
    
    // Registrar nós no autofill tree
    LaunchedEffect(Unit) {
        autofillTree += emailAutofillNode
        autofillTree += passwordAutofillNode
    }
    
    // Commitar autofill quando login for bem sucedido
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            // Notificar o AutofillManager do sistema
            val activity = context as? Activity
            activity?.let {
                val autofillManager = it.getSystemService(AutofillManager::class.java)
                autofillManager?.commit()
            }
        }
    }
    
    // Função para mostrar biometria
    fun showBiometricPrompt() {
        val activity = context as? FragmentActivity ?: return
        val userId = lastLoggedUserId ?: return
        
        val executor = ContextCompat.getMainExecutor(context)
        
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    viewModel.loginWithBiometric(userId)
                }
                
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            }
        )
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticação Biométrica")
            .setSubtitle("Use sua biometria para entrar")
            .setNegativeButtonText("Usar senha")
            .build()
        
        biometricPrompt.authenticate(promptInfo)
    }
    
    // Tentar biometria automaticamente se disponível
    LaunchedEffect(isBiometricAvailable, lastLoggedUserId) {
        if (isBiometricAvailable && lastLoggedUserId != null) {
            showBiometricPrompt()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            // Logo do App
            Surface(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 8.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_splash_icon),
                        contentDescription = "Logo do App",
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Minhas Finanças",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Controle financeiro pessoal",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Formulário
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Entrar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Email com Autofill
                    OutlinedTextField(
                        value = loginForm.email,
                        onValueChange = { viewModel.updateLoginEmail(it) },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                emailAutofillNode.boundingBox = coordinates.boundsInWindow()
                            }
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    autofill?.requestAutofillForNode(emailAutofillNode)
                                } else {
                                    autofill?.cancelAutofillForNode(emailAutofillNode)
                                }
                            }
                            .semantics {
                                contentDescription = "Campo de email para login"
                            },
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Senha com Autofill
                    OutlinedTextField(
                        value = loginForm.senha,
                        onValueChange = { viewModel.updateLoginSenha(it) },
                        label = { Text("Senha") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) 
                                        Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (showPassword) 
                                        "Ocultar senha" else "Mostrar senha"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) 
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { 
                                focusManager.clearFocus()
                                viewModel.loginWithPassword()
                            }
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                passwordAutofillNode.boundingBox = coordinates.boundsInWindow()
                            }
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    autofill?.requestAutofillForNode(passwordAutofillNode)
                                } else {
                                    autofill?.cancelAutofillForNode(passwordAutofillNode)
                                }
                            }
                            .semantics {
                                contentDescription = "Campo de senha para login"
                            },
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Mensagem de erro
                    AnimatedVisibility(visible = loginForm.error != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = loginForm.error ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Botão de login
                    Button(
                        onClick = { viewModel.loginWithPassword() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !loginForm.isLoading,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (loginForm.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Entrar", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    
                    // Biometria
                    if (isBiometricAvailable && lastLoggedUserId != null) {
                        OutlinedButton(
                            onClick = { showBiometricPrompt() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.Fingerprint, 
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Usar biometria", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Link para registro (apenas se permitido)
            if (viewModel.canCreateUsers()) {
                Row(
                    modifier = Modifier.padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Não tem uma conta?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            "Criar conta",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

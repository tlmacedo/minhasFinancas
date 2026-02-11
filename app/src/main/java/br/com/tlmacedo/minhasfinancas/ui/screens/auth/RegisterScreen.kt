package br.com.tlmacedo.minhasfinancas.ui.screens.auth

import android.app.Activity
import android.net.Uri
import android.view.autofill.AutofillManager
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.tlmacedo.minhasfinancas.auth.AuthState
import br.com.tlmacedo.minhasfinancas.auth.BiometricStatus
import br.com.tlmacedo.minhasfinancas.ui.components.common.AvatarPicker
import br.com.tlmacedo.minhasfinancas.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val registerForm by viewModel.registerForm.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val biometricStatus by viewModel.biometricStatus.collectAsState()
    
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val autofill = LocalAutofill.current
    val autofillTree = LocalAutofillTree.current
    
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val isFirstUser = authState == AuthState.NeedSetup
    
    // Nós de autofill para registro
    val nameAutofillNode = remember {
        AutofillNode(
            autofillTypes = listOf(AutofillType.PersonFullName),
            onFill = { viewModel.updateRegisterNome(it) }
        )
    }
    
    val emailAutofillNode = remember {
        AutofillNode(
            autofillTypes = listOf(AutofillType.EmailAddress),
            onFill = { viewModel.updateRegisterEmail(it) }
        )
    }
    
    val newPasswordAutofillNode = remember {
        AutofillNode(
            autofillTypes = listOf(AutofillType.NewPassword),
            onFill = { viewModel.updateRegisterSenha(it) }
        )
    }
    
    // Registrar nós no autofill tree
    LaunchedEffect(Unit) {
        autofillTree += nameAutofillNode
        autofillTree += emailAutofillNode
        autofillTree += newPasswordAutofillNode
    }
    
    // Commitar autofill quando registro for bem sucedido
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            // Notificar o AutofillManager do sistema para salvar credenciais
            val activity = context as? Activity
            activity?.let {
                val autofillManager = it.getSystemService(AutofillManager::class.java)
                autofillManager?.commit()
            }
        }
    }
    
    Scaffold(
        topBar = {
            if (!isFirstUser) {
                TopAppBar(
                    title = { Text("Criar Conta") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
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
                if (isFirstUser) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Bem-vindo!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Crie sua conta para começar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
                
                // Avatar Picker
                AvatarPicker(
                    currentImageUri = selectedImageUri,
                    onImageSelected = { uri ->
                        selectedImageUri = uri
                        viewModel.updateRegisterFotoUri(uri?.toString())
                    },
                    size = 140
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Toque para adicionar foto",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Formulário
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Nome com Autofill
                        OutlinedTextField(
                            value = registerForm.nome,
                            onValueChange = { viewModel.updateRegisterNome(it) },
                            label = { Text("Nome completo") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Person, contentDescription = null)
                            },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    nameAutofillNode.boundingBox = coordinates.boundsInWindow()
                                }
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        autofill?.requestAutofillForNode(nameAutofillNode)
                                    } else {
                                        autofill?.cancelAutofillForNode(nameAutofillNode)
                                    }
                                }
                                .semantics {
                                    contentDescription = "Campo de nome completo"
                                },
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        // Email com Autofill
                        OutlinedTextField(
                            value = registerForm.email,
                            onValueChange = { viewModel.updateRegisterEmail(it) },
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
                                    contentDescription = "Campo de email para registro"
                                },
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        // Senha com Autofill (NewPassword)
                        OutlinedTextField(
                            value = registerForm.senha,
                            onValueChange = { viewModel.updateRegisterSenha(it) },
                            label = { Text("Senha") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Lock, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) 
                                            Icons.Outlined.VisibilityOff 
                                        else 
                                            Icons.Outlined.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) 
                                VisualTransformation.None 
                            else 
                                PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    newPasswordAutofillNode.boundingBox = coordinates.boundsInWindow()
                                }
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        autofill?.requestAutofillForNode(newPasswordAutofillNode)
                                    } else {
                                        autofill?.cancelAutofillForNode(newPasswordAutofillNode)
                                    }
                                }
                                .semantics {
                                    contentDescription = "Campo de nova senha"
                                },
                            shape = RoundedCornerShape(12.dp),
                            supportingText = {
                                Text("Mínimo 6 caracteres")
                            }
                        )
                        
                        // Confirmar Senha
                        OutlinedTextField(
                            value = registerForm.confirmarSenha,
                            onValueChange = { viewModel.updateRegisterConfirmarSenha(it) },
                            label = { Text("Confirmar senha") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Lock, contentDescription = null)
                            },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Indicador de match
                                    if (registerForm.confirmarSenha.isNotEmpty()) {
                                        Icon(
                                            imageVector = if (registerForm.senha == registerForm.confirmarSenha) 
                                                Icons.Default.Check else Icons.Default.Close,
                                            contentDescription = null,
                                            tint = if (registerForm.senha == registerForm.confirmarSenha)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                        Icon(
                                            imageVector = if (showConfirmPassword) 
                                                Icons.Outlined.VisibilityOff 
                                            else 
                                                Icons.Outlined.Visibility,
                                            contentDescription = null
                                        )
                                    }
                                }
                            },
                            visualTransformation = if (showConfirmPassword) 
                                VisualTransformation.None 
                            else 
                                PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { 
                                    focusManager.clearFocus()
                                    viewModel.register()
                                }
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics {
                                    contentDescription = "Confirmar nova senha"
                                },
                            shape = RoundedCornerShape(12.dp),
                            isError = registerForm.confirmarSenha.isNotEmpty() && 
                                     registerForm.senha != registerForm.confirmarSenha
                        )
                        
                        // Opção de Biometria
                        if (biometricStatus == BiometricStatus.Available) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(12.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Usar biometria",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Login rápido com impressão digital ou face",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    
                                    Switch(
                                        checked = registerForm.usarBiometria,
                                        onCheckedChange = { viewModel.updateRegisterUsarBiometria(it) }
                                    )
                                }
                            }
                        }
                        
                        // Erro
                        AnimatedVisibility(visible = registerForm.error != null) {
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
                                        text = registerForm.error ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Botão de registro
                        Button(
                            onClick = { viewModel.register() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !registerForm.isLoading,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (registerForm.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.PersonAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isFirstUser) "Criar conta e entrar" else "Criar conta",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

package br.com.tlmacedo.minhasfinancas.ui.screens.contas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.tlmacedo.minhasfinancas.ui.components.IconSelector
import br.com.tlmacedo.minhasfinancas.ui.components.MoneyTextField
import br.com.tlmacedo.minhasfinancas.ui.viewmodel.ContasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditContaScreen(
    contaId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: ContasViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    val isEditing = contaId != null && contaId > 0
    
    // Carregar dados da conta se editando
    LaunchedEffect(contaId) {
        if (isEditing) {
            viewModel.loadContaForEdit(contaId!!)
        } else {
            viewModel.resetForm()
        }
    }
    
    // Navegar de volta quando salvar
    LaunchedEffect(formState.isSaved) {
        if (formState.isSaved) {
            onNavigateBack()
        }
    }
    
    // Cor selecionada para preview
    val selectedColor = remember(formState.cor) {
        try {
            Color(android.graphics.Color.parseColor(formState.cor))
        } catch (e: Exception) {
            Color(0xFF4CAF50)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Conta" else "Nova Conta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (formState.isLoading && isEditing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Nome da Conta
                OutlinedTextField(
                    value = formState.nome,
                    onValueChange = { viewModel.updateNome(it) },
                    label = { Text("Nome da Conta") },
                    placeholder = { Text("Ex: Nubank, Carteira, Itaú...") },
                    leadingIcon = {
                        Icon(Icons.Default.Badge, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Tipo de Conta
                TipoContaSelector(
                    tiposConta = uiState.tiposConta,
                    selectedId = formState.tipoContaId,
                    onSelect = { viewModel.updateTipoConta(it) }
                )
                
                // Saldo Inicial (somente para nova conta)
                if (!isEditing) {
                    MoneyTextField(
                        value = formState.saldoInicial,
                        onValueChange = { viewModel.updateSaldoInicial(it) },
                        label = "Saldo Inicial",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Seletor de Ícone
                IconSelector(
                    selectedIcon = formState.icone,
                    selectedColor = selectedColor,
                    onIconSelect = { viewModel.updateIcone(it) }
                )
                
                // Cor da Conta
                ColorSelector(
                    selectedColor = formState.cor,
                    onColorSelect = { viewModel.updateCor(it) }
                )
                
                // Incluir no Total
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.updateIncluirNoTotal(!formState.incluirNoTotal) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Incluir no Saldo Total",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Esta conta será somada ao saldo total do app",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Switch(
                            checked = formState.incluirNoTotal,
                            onCheckedChange = { viewModel.updateIncluirNoTotal(it) }
                        )
                    }
                }
                
                // Erro
                formState.error?.let { error ->
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
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Botão Salvar
                Button(
                    onClick = { viewModel.saveConta() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !formState.isLoading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (formState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEditing) "Salvar Alterações" else "Criar Conta",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TipoContaSelector(
    tiposConta: List<br.com.tlmacedo.minhasfinancas.data.local.entity.TipoConta>,
    selectedId: Long?,
    onSelect: (Long) -> Unit
) {
    Column {
        Text(
            text = "Tipo de Conta",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (tiposConta.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tiposConta) { tipo ->
                    val isSelected = tipo.id == selectedId
                    
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelect(tipo.id) },
                        label = { Text(tipo.nome) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSelector(
    selectedColor: String,
    onColorSelect: (String) -> Unit
) {
    val cores = listOf(
        "#4CAF50" to "Verde",
        "#2196F3" to "Azul",
        "#9C27B0" to "Roxo",
        "#FF9800" to "Laranja",
        "#F44336" to "Vermelho",
        "#00BCD4" to "Ciano",
        "#E91E63" to "Rosa",
        "#607D8B" to "Cinza",
        "#795548" to "Marrom",
        "#3F51B5" to "Índigo"
    )
    
    Column {
        Text(
            text = "Cor da Conta",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cores) { (hex, _) ->
                val color = Color(android.graphics.Color.parseColor(hex))
                val isSelected = hex.equals(selectedColor, ignoreCase = true)
                
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color)
                        .then(
                            if (isSelected) {
                                Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onColorSelect(hex) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

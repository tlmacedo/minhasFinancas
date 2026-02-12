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
import br.com.tlmacedo.minhasfinancas.ui.components.BancoSelector
import br.com.tlmacedo.minhasfinancas.ui.components.IconSelector
import br.com.tlmacedo.minhasfinancas.ui.components.MoneyTextField
import br.com.tlmacedo.minhasfinancas.ui.viewmodel.ContasViewModel

/**
 * Tela de formulário para adicionar uma nova conta ou editar uma existente.
 * 
 * Centraliza a lógica de entrada de dados como nome, tipo de conta, saldo inicial,
 * identificação visual (cores e ícones) e preferências de exibição.
 * 
 * @param contaId ID da conta para edição. Se nulo ou <= 0, a tela entra em modo de criação.
 * @param onNavigateBack Callback para retornar à tela anterior após salvar ou cancelar.
 * @param viewModel ViewModel que gerencia o estado do formulário e a persistência.
 */
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
    
    // Inicializa o formulário dependendo do modo (Criação ou Edição)
    LaunchedEffect(contaId) {
        if (isEditing) viewModel.loadContaForEdit(contaId!!) else viewModel.resetForm()
    }
    
    // Observa se a gravação foi concluída com sucesso para fechar a tela
    LaunchedEffect(formState.isSaved) {
        if (formState.isSaved) onNavigateBack()
    }
    
    val selectedColor = remember(formState.cor) {
        try { Color(android.graphics.Color.parseColor(formState.cor)) } catch (e: Exception) { Color(0xFF4CAF50) }
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
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
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
                // Campo: Nome da Conta
                OutlinedTextField(
                    value = formState.nome,
                    onValueChange = { viewModel.updateNome(it) },
                    label = { Text("Nome da Conta") },
                    leadingIcon = { Icon(Icons.Default.Badge, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Seletor: Tipo de Conta (Corrente, Poupança, etc)
                TipoContaSelector(
                    tiposConta = uiState.tiposConta,
                    selectedId = formState.tipoContaId,
                    onSelect = { viewModel.updateTipoConta(it) }
                )
                
                // Campo: Saldo Inicial (Apenas na criação)
                if (!isEditing) {
                    MoneyTextField(
                        value = formState.saldoInicial,
                        onValueChange = { viewModel.updateSaldoInicial(it) },
                        label = "Saldo Inicial",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Seção: Identificação Visual (Ícone/Banco)
                Column {
                    Text(
                        text = "Identificação Visual",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            onClick = { viewModel.setUsarIconeBanco(true) },
                            selected = formState.usarIconeBanco,
                            icon = { Icon(Icons.Default.AccountBalance, null) }
                        ) {
                            Text("Banco")
                        }
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            onClick = { viewModel.setUsarIconeBanco(false) },
                            selected = !formState.usarIconeBanco,
                            icon = { Icon(Icons.Default.Category, null) }
                        ) {
                            Text("Ícone")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Alterna entre seletor de bancos ou seletor de ícones genéricos
                    if (formState.usarIconeBanco) {
                        BancoSelector(
                            selectedBancoId = formState.bancoId,
                            onBancoSelect = { viewModel.updateBancoId(it) }
                        )
                    } else {
                        IconSelector(
                            selectedIcon = formState.icone,
                            selectedColor = selectedColor,
                            onIconSelect = { viewModel.updateIcone(it) }
                        )
                    }
                }
                
                // Seletor: Cor da Conta
                ColorSelector(
                    selectedColor = formState.cor,
                    onColorSelect = { viewModel.updateCor(it) }
                )
                
                // Preferência: Incluir no Saldo Total
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.updateIncluirNoTotal(!formState.incluirNoTotal) }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Calculate, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Incluir no Saldo Total", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text("Esta conta será somada ao saldo total", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = formState.incluirNoTotal, onCheckedChange = { viewModel.updateIncluirNoTotal(it) })
                    }
                }
                
                // Exibição de erros de validação/persistência
                if (formState.error != null) {
                    Text(text = formState.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                
                // Botão de Ação: Salvar
                Button(
                    onClick = { viewModel.saveConta() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !formState.isLoading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (formState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    else {
                        Icon(Icons.Default.Check, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isEditing) "Salvar Alterações" else "Criar Conta", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

/**
 * Componente de seleção horizontal para o Tipo de Conta.
 */
@Composable
private fun TipoContaSelector(tiposConta: List<br.com.tlmacedo.minhasfinancas.data.local.entity.TipoConta>, selectedId: Long?, onSelect: (Long) -> Unit) {
    Column {
        Text("Tipo de Conta", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tiposConta) { tipo ->
                val isSelected = tipo.id == selectedId
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelect(tipo.id) },
                    label = { Text(tipo.nome) },
                    leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) } } else null
                )
            }
        }
    }
}

/**
 * Componente de seleção de cores pré-definidas para a conta.
 */
@Composable
private fun ColorSelector(selectedColor: String, onColorSelect: (String) -> Unit) {
    val cores = listOf("#4CAF50", "#2196F3", "#9C27B0", "#FF9800", "#F44336", "#00BCD4", "#E91E63", "#607D8B", "#795548", "#3F51B5")
    Column {
        Text("Cor da Conta", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(cores) { hex ->
                val color = Color(android.graphics.Color.parseColor(hex))
                val isSelected = hex.equals(selectedColor, ignoreCase = true)
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(color)
                        .then(if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape) else Modifier)
                        .clickable { onColorSelect(hex) },
                    contentAlignment = Alignment.Center
                ) { if (isSelected) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(24.dp)) }
            }
        }
    }
}

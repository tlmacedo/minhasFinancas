package br.com.tlmacedo.minhasfinancas.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.tlmacedo.minhasfinancas.ui.components.common.AnimatedCurrency
import br.com.tlmacedo.minhasfinancas.ui.components.ContaIcons
import br.com.tlmacedo.minhasfinancas.ui.theme.*
import br.com.tlmacedo.minhasfinancas.ui.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToContas: () -> Unit,
    onNavigateToEventos: () -> Unit,
    onNavigateToCategorias: () -> Unit,
    onNavigateToRelatorios: () -> Unit,
    onNavigateToConfiguracoes: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header com saudação
            item {
                HeaderSection()
            }
            
            // Card de saldo principal
            item {
                BalanceCard(
                    saldoTotal = uiState.saldoTotal,
                    receitasMes = uiState.receitasMes,
                    despesasMes = uiState.despesasMes
                )
            }
            
            // Ações rápidas
            item {
                QuickActionsSection(
                    onNavigateToContas = onNavigateToContas,
                    onNavigateToEventos = onNavigateToEventos,
                    onNavigateToCategorias = onNavigateToCategorias,
                    onNavigateToRelatorios = onNavigateToRelatorios,
                    onNavigateToConfiguracoes = onNavigateToConfiguracoes
                )
            }
            
            // Minhas Contas
            item {
                SectionHeader(
                    title = "Minhas Contas",
                    actionText = "Ver todas",
                    onActionClick = onNavigateToContas
                )
            }
            
            item {
                if (uiState.contas.isEmpty()) {
                    EmptyStateCard(
                        icon = Icons.Outlined.AccountBalance,
                        message = "Nenhuma conta cadastrada",
                        actionText = "Adicionar conta",
                        onActionClick = onNavigateToContas
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.contas.take(5)) { contaComTipo ->
                            MiniContaCard(
                                nome = contaComTipo.conta.nome,
                                saldo = contaComTipo.conta.saldoAtual,
                                icone = contaComTipo.conta.icone,
                                cor = contaComTipo.conta.cor,
                                onClick = onNavigateToContas
                            )
                        }
                    }
                }
            }
            
            // Últimas transações
            item {
                SectionHeader(
                    title = "Últimas Transações",
                    actionText = "Ver todas",
                    onActionClick = onNavigateToEventos
                )
            }
            
            if (uiState.ultimosEventos.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Outlined.Receipt,
                        message = "Nenhuma transação registrada",
                        actionText = "Adicionar transação",
                        onActionClick = onNavigateToEventos
                    )
                }
            } else {
                items(uiState.ultimosEventos.take(5)) { evento ->
                    TransacaoItem(
                        descricao = evento.evento.descricao,
                        valor = evento.evento.valor,
                        tipo = evento.evento.tipo,
                        categoria = evento.categoria?.nome ?: "Sem categoria",
                        conta = evento.conta.nome
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Bom dia"
            hour < 18 -> "Boa tarde"
            else -> "Boa noite"
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Minhas Finanças",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun BalanceCard(
    saldoTotal: Double,
    receitasMes: Double,
    despesasMes: Double
) {
    val gradientColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(gradientColors)
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Saldo Total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                AnimatedCurrency(
                    targetValue = saldoTotal,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Receitas
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Receitas",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                                .format(receitasMes),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Despesas
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Despesas",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                                .format(despesasMes),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    onNavigateToContas: () -> Unit,
    onNavigateToEventos: () -> Unit,
    onNavigateToCategorias: () -> Unit,
    onNavigateToRelatorios: () -> Unit,
    onNavigateToConfiguracoes: () -> Unit
) {
    val actions = listOf(
        QuickAction("Contas", Icons.Outlined.AccountBalance, onNavigateToContas),
        QuickAction("Transações", Icons.Outlined.SwapHoriz, onNavigateToEventos),
        QuickAction("Categorias", Icons.Outlined.Category, onNavigateToCategorias),
        QuickAction("Relatórios", Icons.Outlined.BarChart, onNavigateToRelatorios),
        QuickAction("Config.", Icons.Outlined.Settings, onNavigateToConfiguracoes)
    )
    
    LazyRow(
        modifier = Modifier.padding(vertical = 20.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(actions) { action ->
            QuickActionItem(
                label = action.label,
                icon = action.icon,
                onClick = action.onClick
            )
        }
    }
}

private data class QuickAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
private fun QuickActionItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        TextButton(onClick = onActionClick) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun MiniContaCard(
    nome: String,
    saldo: Double,
    icone: String?,
    cor: String?,
    onClick: () -> Unit
) {
    val contaCor = remember(cor) {
        try {
            cor?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color(0xFF6750A4)
        } catch (e: Exception) {
            Color(0xFF6750A4)
        }
    }
    
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = contaCor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(contaCor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ContaIcons.getIcon(icone),
                    contentDescription = null,
                    tint = contaCor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = nome,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            
            Text(
                text = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(saldo),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (saldo >= 0) contaCor else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun TransacaoItem(
    descricao: String,
    valor: Double,
    tipo: String,
    categoria: String,
    conta: String
) {
    val isReceita = tipo == "RECEITA"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isReceita) IncomeGreenLight else ExpenseRedLight
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isReceita) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (isReceita) IncomeGreen else ExpenseRed,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = "$categoria • $conta",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Valor
            Text(
                text = (if (isReceita) "+ " else "- ") + 
                    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isReceita) IncomeGreen else ExpenseRed
            )
        }
    }
}

@Composable
private fun EmptyStateCard(
    icon: ImageVector,
    message: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onActionClick) {
                Text(actionText)
            }
        }
    }
}

package br.com.tlmacedo.minhasfinancas.ui.screens.eventos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNovoEvento: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transações") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Filtros */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNovoEvento,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova transação")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cabeçalho do mês
            item {
                Text(
                    text = "Fevereiro 2026",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Lista de transações mockadas
            items(20) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = when (index % 5) {
                                    0 -> "Salário"
                                    1 -> "Supermercado Extra"
                                    2 -> "Conta de Luz"
                                    3 -> "Uber"
                                    else -> "Netflix"
                                },
                                fontWeight = FontWeight.Medium
                            )
                        },
                        supportingContent = {
                            Text(
                                text = when (index % 5) {
                                    0 -> "Receita • Conta Corrente"
                                    1 -> "Alimentação • Cartão Crédito"
                                    2 -> "Moradia • Débito Automático"
                                    3 -> "Transporte • Cartão Crédito"
                                    else -> "Lazer • Cartão Crédito"
                                }
                            )
                        },
                        trailingContent = {
                            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                                Text(
                                    text = when (index % 5) {
                                        0 -> "+R$ 5.500,00"
                                        1 -> "-R$ 487,32"
                                        2 -> "-R$ 245,80"
                                        3 -> "-R$ 28,90"
                                        else -> "-R$ 55,90"
                                    },
                                    color = if (index % 5 == 0)
                                        MaterialTheme.colorScheme.tertiary
                                    else
                                        MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${(index % 28) + 1}/02",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        leadingContent = {
                            Icon(
                                imageVector = when (index % 5) {
                                    0 -> Icons.Default.Work
                                    1 -> Icons.Default.ShoppingCart
                                    2 -> Icons.Default.Home
                                    3 -> Icons.Default.DirectionsCar
                                    else -> Icons.Default.Tv
                                },
                                contentDescription = null,
                                tint = if (index % 5 == 0)
                                    MaterialTheme.colorScheme.tertiary
                                else
                                    MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }
}

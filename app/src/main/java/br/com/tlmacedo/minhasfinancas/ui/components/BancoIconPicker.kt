package br.com.tlmacedo.minhasfinancas.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import br.com.tlmacedo.minhasfinancas.R

/**
 * Componente para exibir o ícone do banco selecionado
 */
@Composable
fun BancoIconDisplay(
    bancoId: String?,
    modifier: Modifier = Modifier,
    size: Int = 48,
    onClick: (() -> Unit)? = null
) {
    val iconRes = BancoIcons.getIcone(bancoId)
    
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = BancoIcons.getBanco(bancoId)?.nome ?: "Banco",
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Campo de seleção de ícone de banco com preview
 */
@Composable
fun BancoIconSelector(
    selectedBancoId: String?,
    onBancoSelected: (BancoIcons.BancoInfo?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Ícone do Banco"
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedBanco = BancoIcons.getBanco(selectedBancoId)

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { showDialog = true }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BancoIconDisplay(
                bancoId = selectedBancoId,
                size = 40
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = selectedBanco?.nome ?: "Selecione um banco",
                style = MaterialTheme.typography.bodyLarge,
                color = if (selectedBanco != null) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.weight(1f)
            )
            
            if (selectedBanco != null) {
                TextButton(
                    onClick = { onBancoSelected(null) }
                ) {
                    Text("Limpar")
                }
            }
        }
    }

    if (showDialog) {
        BancoIconPickerDialog(
            selectedBancoId = selectedBancoId,
            onDismiss = { showDialog = false },
            onBancoSelected = { banco ->
                onBancoSelected(banco)
                showDialog = false
            }
        )
    }
}

/**
 * Dialog para seleção de ícone de banco
 */
@Composable
fun BancoIconPickerDialog(
    selectedBancoId: String?,
    onDismiss: () -> Unit,
    onBancoSelected: (BancoIcons.BancoInfo?) -> Unit
) {
    val bancos = BancoIcons.getAllBancos()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Selecione o Banco",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Opção "Nenhum" / Genérico
                    item {
                        BancoIconItem(
                            banco = null,
                            isSelected = selectedBancoId == null,
                            onClick = { onBancoSelected(null) }
                        )
                    }
                    
                    items(bancos) { banco ->
                        BancoIconItem(
                            banco = banco,
                            isSelected = banco.id == selectedBancoId,
                            onClick = { onBancoSelected(banco) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

/**
 * Item individual de banco no grid
 */
@Composable
private fun BancoIconItem(
    banco: BancoIcons.BancoInfo?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconRes = banco?.iconRes ?: R.drawable.ic_banco_generico
    val nome = banco?.nome ?: "Outro"

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.Transparent
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = nome,
                modifier = Modifier.size(48.dp)
            )
            
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selecionado",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = nome,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

/**
 * Componente que observa o nome da conta e sugere automaticamente o banco
 */
@Composable
fun BancoAutoDetect(
    nomeConta: String,
    currentBancoId: String?,
    onBancoDetected: (BancoIcons.BancoInfo) -> Unit
) {
    LaunchedEffect(nomeConta) {
        if (currentBancoId == null && nomeConta.length >= 3) {
            val bancoDetectado = BancoIcons.identificarBanco(nomeConta)
            if (bancoDetectado != null) {
                onBancoDetected(bancoDetectado)
            }
        }
    }
}

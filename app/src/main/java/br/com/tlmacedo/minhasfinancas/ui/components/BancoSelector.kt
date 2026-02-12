package br.com.tlmacedo.minhasfinancas.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.tlmacedo.minhasfinancas.data.model.Banco
import br.com.tlmacedo.minhasfinancas.data.model.BancosData

/**
 * Componente para exibir o ícone de um banco
 */
@Composable
fun BancoIcon(
    bancoId: String?,
    modifier: Modifier = Modifier,
    size: Int = 40,
    showBackground: Boolean = true
) {
    val context = LocalContext.current
    val banco = bancoId?.let { BancosData.getBancoById(it) }
    
    if (banco != null) {
        val resourceId = context.resources.getIdentifier(
            banco.iconRes,
            "drawable",
            context.packageName
        )
        
        val corBanco = try {
            Color(android.graphics.Color.parseColor(banco.corPrimaria))
        } catch (e: Exception) {
            MaterialTheme.colorScheme.primary
        }
        
        Box(
            modifier = modifier
                .size(size.dp)
                .then(
                    if (showBackground) {
                        Modifier
                            .clip(CircleShape)
                            .background(corBanco.copy(alpha = 0.1f))
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (resourceId != 0) {
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = banco.nome,
                    modifier = Modifier.size((size * 0.6).dp)
                )
            } else {
                Text(
                    text = banco.nome.first().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = corBanco
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .size(size.dp)
                .then(
                    if (showBackground) {
                        Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountBalance,
                contentDescription = "Sem banco",
                modifier = Modifier.size((size * 0.5).dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Seletor de banco com lista horizontal
 */
@Composable
fun BancoSelector(
    selectedBancoId: String?,
    onBancoSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val bancos = remember { BancosData.bancosBrasileiros }
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Banco (opcional)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (selectedBancoId != null) {
                TextButton(
                    onClick = { onBancoSelect(null) },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Limpar", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(bancos, key = { it.id }) { banco ->
                BancoItem(
                    banco = banco,
                    isSelected = banco.id == selectedBancoId,
                    onClick = { onBancoSelect(banco.id) }
                )
            }
        }
    }
}

@Composable
private fun BancoItem(
    banco: Banco,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(
        banco.iconRes,
        "drawable",
        context.packageName
    )
    
    val corBanco = remember(banco.corPrimaria) {
        try {
            Color(android.graphics.Color.parseColor(banco.corPrimaria))
        } catch (e: Exception) {
            Color(0xFF4CAF50)
        }
    }
    
    Column(
        modifier = Modifier
            .width(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier
                        .background(corBanco.copy(alpha = 0.1f))
                        .border(2.dp, corBanco, RoundedCornerShape(12.dp))
                } else {
                    Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                }
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) corBanco.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surface
                ),
            contentAlignment = Alignment.Center
        ) {
            if (resourceId != 0) {
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = banco.nome,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Text(
                    text = banco.nome.take(2).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = corBanco
                )
            }
            
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(corBanco),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = banco.nomeExibicao,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) corBanco else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

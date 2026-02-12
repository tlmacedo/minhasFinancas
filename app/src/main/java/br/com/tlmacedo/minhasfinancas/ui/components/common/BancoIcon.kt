package br.com.tlmacedo.minhasfinancas.ui.components.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.com.tlmacedo.minhasfinancas.R
import br.com.tlmacedo.minhasfinancas.ui.components.BancoIcons

@Composable
fun BancoIcon(
    bancoId: String?,
    modifier: Modifier = Modifier,
    size: Int = 40,
    backgroundColor: Color? = null
) {
    val bancoInfo = BancoIcons.getBanco(bancoId)
    val containerColor = backgroundColor ?: bancoInfo?.corPrimaria?.let { 
        try { Color(android.graphics.Color.parseColor(it)).copy(alpha = 0.15f) } catch (e: Exception) { null }
    } ?: MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        if (bancoInfo != null) {
            Image(
                painter = painterResource(id = bancoInfo.iconRes),
                contentDescription = bancoInfo.nome,
                modifier = Modifier.size((size * 0.6).dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size((size * 0.5).dp)
            )
        }
    }
}

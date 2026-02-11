package br.com.tlmacedo.minhasfinancas.ui.components.common

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import java.text.NumberFormat
import java.util.*

@Composable
fun AnimatedCurrency(
    targetValue: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    prefix: String = "R$ ",
    animationDuration: Int = 800
) {
    var oldValue by remember { mutableDoubleStateOf(0.0) }
    val animatedValue by animateFloatAsState(
        targetValue = targetValue.toFloat(),
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = FastOutSlowInEasing
        ),
        label = "currency_animation"
    )
    
    LaunchedEffect(targetValue) {
        oldValue = targetValue
    }
    
    val formatter = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR")).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }
    }
    
    Text(
        text = formatter.format(animatedValue),
        style = style,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun AnimatedPercentage(
    targetValue: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    positiveColor: Color = Color(0xFF2E7D32),
    negativeColor: Color = Color(0xFFC62828),
    animationDuration: Int = 600
) {
    val animatedValue by animateFloatAsState(
        targetValue = targetValue.toFloat(),
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = FastOutSlowInEasing
        ),
        label = "percentage_animation"
    )
    
    val color = if (animatedValue >= 0) positiveColor else negativeColor
    val prefix = if (animatedValue >= 0) "+" else ""
    
    Text(
        text = "$prefix${String.format(Locale.getDefault(), "%.1f", animatedValue)}%",
        style = style,
        color = color,
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}

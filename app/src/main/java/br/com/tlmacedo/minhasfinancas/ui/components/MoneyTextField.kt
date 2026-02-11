package br.com.tlmacedo.minhasfinancas.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun MoneyTextField(
    value: Long, // Valor em centavos
    onValueChange: (Long) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    val decimalFormat = remember {
        DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("pt", "BR")))
    }
    
    // Formatar valor para exibição (sem R$) - vazio se zero
    val formattedValue = remember(value) {
        if (value == 0L) "" else decimalFormat.format(value / 100.0)
    }
    
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(formattedValue, TextRange(formattedValue.length)))
    }
    
    // Atualizar quando o valor externo mudar
    LaunchedEffect(value) {
        val newFormatted = if (value == 0L) "" else decimalFormat.format(value / 100.0)
        if (textFieldValue.text != newFormatted) {
            textFieldValue = TextFieldValue(newFormatted, TextRange(newFormatted.length))
        }
    }
    
    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            // Extrair apenas números do input
            val digitsOnly = newValue.text.filter { it.isDigit() }
            
            // Limitar a um valor razoável (999.999.999,99)
            val limitedDigits = digitsOnly.take(11)
            
            // Converter para centavos
            val centavos = limitedDigits.toLongOrNull() ?: 0L
            
            // Formatar para exibição - vazio se zero
            val formatted = if (centavos == 0L) "" else decimalFormat.format(centavos / 100.0)
            
            // Atualizar estado local e notificar mudança
            textFieldValue = TextFieldValue(formatted, TextRange(formatted.length))
            onValueChange(centavos)
        },
        label = { Text(label) },
        prefix = if (textFieldValue.text.isNotEmpty()) {
            { Text("R$ ") }
        } else null,
        placeholder = { 
            Text(
                text = "R$ 0,00",
                style = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            ) 
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() }
        ),
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp)
    )
}

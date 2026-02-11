package br.com.tlmacedo.minhasfinancas.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * VisualTransformation para exibir valores monetários no formato brasileiro
 * Input: "6930" -> Display: "69,30"
 * Input: "69" -> Display: "0,69"
 */
class CurrencyVisualTransformation : VisualTransformation {
    
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text.filter { it.isDigit() }
        val formattedText = formatToCurrency(originalText)
        
        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = CurrencyOffsetMapping(originalText, formattedText)
        )
    }
    
    private fun formatToCurrency(text: String): String {
        if (text.isEmpty()) return "0,00"
        
        val value = text.toLongOrNull() ?: 0L
        val integerPart = value / 100
        val decimalPart = value % 100
        
        // Formata a parte inteira com separador de milhar
        val formattedInteger = formatWithThousandSeparator(integerPart)
        
        // Formata a parte decimal sempre com 2 dígitos
        val formattedDecimal = decimalPart.toString().padStart(2, '0')
        
        return "$formattedInteger,$formattedDecimal"
    }
    
    private fun formatWithThousandSeparator(value: Long): String {
        return value.toString()
            .reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
    }
    
    private class CurrencyOffsetMapping(
        private val originalText: String,
        private val formattedText: String
    ) : OffsetMapping {
        
        override fun originalToTransformed(offset: Int): Int {
            return formattedText.length
        }
        
        override fun transformedToOriginal(offset: Int): Int {
            return originalText.length
        }
    }
}

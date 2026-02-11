package br.com.tlmacedo.minhasfinancas.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    
    private val brazilLocale = Locale("pt", "BR")
    private val currencyFormat = NumberFormat.getCurrencyInstance(brazilLocale)
    private val numberFormat = NumberFormat.getNumberInstance(brazilLocale).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    
    /**
     * Converte centavos (Long) para valor Double
     * Ex: 6930 -> 69.30
     */
    fun centavosToDouble(centavos: Long): Double {
        return centavos / 100.0
    }
    
    /**
     * Converte valor Double para centavos (Long)
     * Ex: 69.30 -> 6930
     */
    fun doubleToCentavos(value: Double): Long {
        return (value * 100).toLong()
    }
    
    /**
     * Formata centavos para exibição com símbolo R$
     * Ex: 6930 -> "R$ 69,30"
     */
    fun formatCentavos(centavos: Long): String {
        return currencyFormat.format(centavosToDouble(centavos))
    }
    
    /**
     * Formata Double para exibição com símbolo R$
     * Ex: 69.30 -> "R$ 69,30"
     */
    fun formatCurrency(value: Double): String {
        return currencyFormat.format(value)
    }
    
    /**
     * Formata centavos para exibição SEM símbolo R$
     * Ex: 6930 -> "69,30"
     */
    fun formatCentavosWithoutSymbol(centavos: Long): String {
        return numberFormat.format(centavosToDouble(centavos))
    }
    
    /**
     * Formata string de dígitos para exibição monetária
     * Ex: "6930" -> "69,30"
     * Ex: "69" -> "0,69"
     * Ex: "5530" -> "55,30"
     */
    fun formatInputToDisplay(input: String): String {
        val cleanInput = input.filter { it.isDigit() }
        if (cleanInput.isEmpty()) return "0,00"
        
        val centavos = cleanInput.toLongOrNull() ?: 0L
        return formatCentavosWithoutSymbol(centavos)
    }
    
    /**
     * Converte string de dígitos para centavos
     * Ex: "6930" -> 6930
     * Ex: "69" -> 69
     */
    fun inputToCentavos(input: String): Long {
        val cleanInput = input.filter { it.isDigit() }
        return cleanInput.toLongOrNull() ?: 0L
    }
    
    /**
     * Converte string de dígitos para Double
     * Ex: "6930" -> 69.30
     * Ex: "69" -> 0.69
     */
    fun inputToDouble(input: String): Double {
        return centavosToDouble(inputToCentavos(input))
    }
}

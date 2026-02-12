package br.com.tlmacedo.minhasfinancas.ui.components

import androidx.annotation.DrawableRes
import br.com.tlmacedo.minhasfinancas.R

/**
 * Gerenciador de ícones de bancos brasileiros
 * Mapeia nomes de bancos para seus respectivos ícones
 */
object BancoIcons {

    data class BancoInfo(
        val id: String,
        val nome: String,
        val nomeCompleto: String,
        @DrawableRes val iconRes: Int,
        val codigoBanco: String? = null, // Código FEBRABAN
        val corPrimaria: String // Cor hexadecimal
    )

    // Lista de bancos com seus ícones
    val bancos = listOf(
        BancoInfo("nubank", "Nubank", "Nu Pagamentos S.A.", R.drawable.ic_banco_nubank, "260", "#820AD1"),
        BancoInfo("bb", "Banco do Brasil", "Banco do Brasil S.A.", R.drawable.ic_banco_bb, "001", "#FFED00"),
        BancoInfo("itau", "Itaú", "Itaú Unibanco S.A.", R.drawable.ic_banco_itau, "341", "#EC7000"),
        BancoInfo("bradesco", "Bradesco", "Banco Bradesco S.A.", R.drawable.ic_banco_bradesco, "237", "#CC092F"),
        BancoInfo("caixa", "Caixa", "Caixa Econômica Federal", R.drawable.ic_banco_caixa, "104", "#005CA9"),
        BancoInfo("santander", "Santander", "Banco Santander Brasil S.A.", R.drawable.ic_banco_santander, "033", "#EC0000"),
        BancoInfo("inter", "Inter", "Banco Inter S.A.", R.drawable.ic_banco_inter, "077", "#FF7A00"),
        BancoInfo("sicoob", "Sicoob", "Sicoob", R.drawable.ic_banco_sicoob, "756", "#003641"),
        BancoInfo("sicredi", "Sicredi", "Sicredi", R.drawable.ic_banco_sicredi, "748", "#8DC63F"),
        BancoInfo("original", "Original", "Banco Original S.A.", R.drawable.ic_banco_original, "212", "#00A650"),
        BancoInfo("safra", "Safra", "Banco Safra S.A.", R.drawable.ic_banco_safra, "422", "#00205B"),
        BancoInfo("banrisul", "Banrisul", "Banco do Estado do Rio Grande do Sul S.A.", R.drawable.ic_banco_banrisul, "041", "#004B8D"),
        BancoInfo("amazonia", "Banco da Amazônia", "Banco da Amazônia S.A.", R.drawable.ic_banco_amazonia, "003", "#00A859"),
        BancoInfo("nordeste", "BNB", "Banco do Nordeste do Brasil S.A.", R.drawable.ic_banco_nordeste, "004", "#E31837"),
        BancoInfo("c6", "C6 Bank", "C6 Bank", R.drawable.ic_banco_c6, "336", "#242424"),
        BancoInfo("pagbank", "PagBank", "PagSeguro Internet S.A.", R.drawable.ic_banco_pagbank, "290", "#00AB4E"),
        BancoInfo("mercadopago", "Mercado Pago", "Mercado Pago", R.drawable.ic_banco_mercadopago, "323", "#00B1EA"),
        BancoInfo("picpay", "PicPay", "PicPay Serviços S.A.", R.drawable.ic_banco_picpay, "380", "#21C25E"),
        BancoInfo("neon", "Neon", "Banco Neon S.A.", R.drawable.ic_banco_neon, "735", "#0066FF"),
        BancoInfo("next", "Next", "Next", R.drawable.ic_banco_next, "237", "#00E676")
    )

    // Mapa para busca rápida por ID
    private val bancosPorId = bancos.associateBy { it.id }
    
    // Mapa para busca por código FEBRABAN
    private val bancosPorCodigo = bancos.filter { it.codigoBanco != null }
        .associateBy { it.codigoBanco!! }

    /**
     * Obtém o ícone do banco pelo ID
     */
    @DrawableRes
    fun getIcone(bancoId: String?): Int {
        return bancosPorId[bancoId]?.iconRes ?: R.drawable.ic_banco_generico
    }

    /**
     * Obtém informações do banco pelo ID
     */
    fun getBanco(bancoId: String?): BancoInfo? {
        return bancosPorId[bancoId]
    }

    /**
     * Obtém banco pelo código FEBRABAN
     */
    fun getBancoPorCodigo(codigo: String?): BancoInfo? {
        return bancosPorCodigo[codigo]
    }

    /**
     * Tenta identificar o banco pelo nome digitado
     * Retorna o BancoInfo se encontrar correspondência
     */
    fun identificarBanco(nome: String): BancoInfo? {
        val nomeLower = nome.lowercase().trim()
        
        // Palavras-chave para cada banco
        val keywords = mapOf(
            "nubank" to listOf("nubank", "nu bank", "roxinho"),
            "bb" to listOf("banco do brasil", "bb", "brasil"),
            "itau" to listOf("itaú", "itau", "itaú unibanco"),
            "bradesco" to listOf("bradesco", "bra"),
            "caixa" to listOf("caixa", "cef", "caixa economica", "caixa econômica"),
            "santander" to listOf("santander", "san"),
            "inter" to listOf("inter", "banco inter"),
            "sicoob" to listOf("sicoob"),
            "sicredi" to listOf("sicredi"),
            "original" to listOf("original", "banco original"),
            "safra" to listOf("safra", "banco safra"),
            "banrisul" to listOf("banrisul"),
            "amazonia" to listOf("amazonia", "amazônia", "basa", "banco da amazonia", "banco da amazônia"),
            "nordeste" to listOf("nordeste", "bnb", "banco do nordeste"),
            "c6" to listOf("c6", "c6 bank", "c6bank"),
            "pagbank" to listOf("pagbank", "pagseguro", "pag bank"),
            "mercadopago" to listOf("mercado pago", "mercadopago", "mp"),
            "picpay" to listOf("picpay", "pic pay"),
            "neon" to listOf("neon", "banco neon"),
            "next" to listOf("next")
        )

        for ((bancoId, palavras) in keywords) {
            if (palavras.any { nomeLower.contains(it) }) {
                return bancosPorId[bancoId]
            }
        }

        return null
    }

    /**
     * Retorna todos os bancos disponíveis para seleção
     */
    fun getAllBancos(): List<BancoInfo> = bancos

    /**
     * Verifica se um ícone é de banco (para diferenciar de ícones genéricos)
     */
    fun isBancoIcon(iconeName: String?): Boolean {
        return bancosPorId.containsKey(iconeName)
    }
}

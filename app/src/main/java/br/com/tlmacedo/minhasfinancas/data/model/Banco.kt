package br.com.tlmacedo.minhasfinancas.data.model

/**
 * Modelo de dados para representar um banco brasileiro
 */
data class Banco(
    val id: String,
    val codigo: String,
    val nome: String,
    val nomeExibicao: String,
    val iconRes: String,
    val corPrimaria: String,
    val corSecundaria: String? = null
)

/**
 * Repositório estático com os principais bancos brasileiros
 */
object BancosData {
    
    val bancosBrasileiros = listOf(
        Banco(
            id = "nubank",
            codigo = "260",
            nome = "Nubank",
            nomeExibicao = "Nubank",
            iconRes = "ic_banco_nubank",
            corPrimaria = "#8A05BE"
        ),
        Banco(
            id = "itau",
            codigo = "341",
            nome = "Itaú Unibanco",
            nomeExibicao = "Itaú",
            iconRes = "ic_banco_itau",
            corPrimaria = "#FF6600",
            corSecundaria = "#003399"
        ),
        Banco(
            id = "bb",
            codigo = "001",
            nome = "Banco do Brasil",
            nomeExibicao = "BB",
            iconRes = "ic_banco_bb",
            corPrimaria = "#FFCC00",
            corSecundaria = "#003399"
        ),
        Banco(
            id = "bradesco",
            codigo = "237",
            nome = "Bradesco",
            nomeExibicao = "Bradesco",
            iconRes = "ic_banco_bradesco",
            corPrimaria = "#CC092F"
        ),
        Banco(
            id = "santander",
            codigo = "033",
            nome = "Santander",
            nomeExibicao = "Santander",
            iconRes = "ic_banco_santander",
            corPrimaria = "#EC0000"
        ),
        Banco(
            id = "caixa",
            codigo = "104",
            nome = "Caixa Econômica Federal",
            nomeExibicao = "Caixa",
            iconRes = "ic_banco_caixa",
            corPrimaria = "#005CA9",
            corSecundaria = "#F37021"
        ),
        Banco(
            id = "inter",
            codigo = "077",
            nome = "Banco Inter",
            nomeExibicao = "Inter",
            iconRes = "ic_banco_inter",
            corPrimaria = "#FF7A00"
        ),
        Banco(
            id = "c6",
            codigo = "336",
            nome = "C6 Bank",
            nomeExibicao = "C6",
            iconRes = "ic_banco_c6",
            corPrimaria = "#1A1A1A"
        ),
        Banco(
            id = "btg",
            codigo = "208",
            nome = "BTG Pactual",
            nomeExibicao = "BTG",
            iconRes = "ic_banco_btg",
            corPrimaria = "#001E62"
        ),
        Banco(
            id = "picpay",
            codigo = "380",
            nome = "PicPay",
            nomeExibicao = "PicPay",
            iconRes = "ic_banco_picpay",
            corPrimaria = "#21C25E"
        ),
        Banco(
            id = "mercadopago",
            codigo = "323",
            nome = "Mercado Pago",
            nomeExibicao = "MP",
            iconRes = "ic_banco_mercadopago",
            corPrimaria = "#00B1EA"
        ),
        Banco(
            id = "pagbank",
            codigo = "290",
            nome = "PagBank",
            nomeExibicao = "PagBank",
            iconRes = "ic_banco_pagbank",
            corPrimaria = "#00A94F",
            corSecundaria = "#FFC20E"
        ),
        Banco(
            id = "neon",
            codigo = "735",
            nome = "Neon",
            nomeExibicao = "Neon",
            iconRes = "ic_banco_neon",
            corPrimaria = "#00E5CF"
        ),
        Banco(
            id = "next",
            codigo = "237",
            nome = "Next",
            nomeExibicao = "Next",
            iconRes = "ic_banco_next",
            corPrimaria = "#00FF5F"
        ),
        Banco(
            id = "original",
            codigo = "212",
            nome = "Banco Original",
            nomeExibicao = "Original",
            iconRes = "ic_banco_original",
            corPrimaria = "#F5821F"
        ),
        Banco(
            id = "sicoob",
            codigo = "756",
            nome = "Sicoob",
            nomeExibicao = "Sicoob",
            iconRes = "ic_banco_sicoob",
            corPrimaria = "#003641",
            corSecundaria = "#6CC04A"
        ),
        Banco(
            id = "sicredi",
            codigo = "748",
            nome = "Sicredi",
            nomeExibicao = "Sicredi",
            iconRes = "ic_banco_sicredi",
            corPrimaria = "#008C44",
            corSecundaria = "#FFD100"
        ),
        Banco(
            id = "safra",
            codigo = "422",
            nome = "Banco Safra",
            nomeExibicao = "Safra",
            iconRes = "ic_banco_safra",
            corPrimaria = "#00447C"
        ),
        Banco(
            id = "pan",
            codigo = "623",
            nome = "Banco Pan",
            nomeExibicao = "Pan",
            iconRes = "ic_banco_pan",
            corPrimaria = "#00A1E0"
        ),
        Banco(
            id = "outro",
            codigo = "000",
            nome = "Outro Banco",
            nomeExibicao = "Outro",
            iconRes = "ic_banco_outro",
            corPrimaria = "#607D8B"
        )
    )
    
    /**
     * Busca um banco pelo ID
     */
    fun getBancoById(id: String): Banco? {
        return bancosBrasileiros.find { it.id == id }
    }
    
    /**
     * Busca bancos por nome (parcial, case-insensitive)
     */
    fun searchBancos(query: String): List<Banco> {
        val lowerQuery = query.lowercase()
        return bancosBrasileiros.filter { 
            it.nome.lowercase().contains(lowerQuery) ||
            it.nomeExibicao.lowercase().contains(lowerQuery) ||
            it.codigo.contains(lowerQuery)
        }
    }
}

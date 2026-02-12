package br.com.tlmacedo.minhasfinancas.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade que representa uma Conta Financeira no sistema.
 * 
 * Uma conta pode ser um banco, carteira de dinheiro, investimentos, etc.
 * Possui um relacionamento com [TipoConta] através da chave estrangeira [tipoContaId].
 * 
 * @property id Identificador único da conta (Auto-incremento).
 * @property nome Nome descritivo da conta (ex: "Nubank", "Carteira").
 * @property tipoContaId Referência ao tipo de conta (Corrente, Poupança, etc).
 * @property saldoInicial Saldo definido no momento da criação da conta.
 * @property saldoAtual Saldo calculado dinamicamente com base nas transações.
 * @property cor Código hexadecimal da cor para identificação visual na UI.
 * @property icone Nome do ícone do Material Design associado à conta.
 * @property bancoId Identificador opcional de banco específico para exibição de logo.
 * @property incluirNoTotal Define se o saldo desta conta soma no patrimônio total do usuário.
 * @property ativa Status da conta para exclusão lógica ou arquivamento.
 * @property dataCriacao Timestamp do momento em que a conta foi registrada.
 */
@Entity(
    tableName = "contas",
    foreignKeys = [
        ForeignKey(
            entity = TipoConta::class,
            parentColumns = ["id"],
            childColumns = ["tipoContaId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["tipoContaId"])]
)
data class Conta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val tipoContaId: Long,
    val saldoInicial: Double = 0.0,
    val saldoAtual: Double = 0.0,
    val cor: String? = "#4CAF50",
    val icone: String? = "account_balance_wallet",
    val bancoId: String? = null,
    val incluirNoTotal: Boolean = true,
    val ativa: Boolean = true,
    val dataCriacao: Long = System.currentTimeMillis()
)

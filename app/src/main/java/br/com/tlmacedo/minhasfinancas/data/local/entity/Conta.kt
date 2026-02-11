package br.com.tlmacedo.minhasfinancas.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val incluirNoTotal: Boolean = true,
    val ativa: Boolean = true,
    val dataCriacao: Long = System.currentTimeMillis()
)

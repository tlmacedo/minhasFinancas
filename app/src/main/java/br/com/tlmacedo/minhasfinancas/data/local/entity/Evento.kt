package br.com.tlmacedo.minhasfinancas.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "eventos",
    foreignKeys = [
        ForeignKey(
            entity = Conta::class,
            parentColumns = ["id"],
            childColumns = ["contaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["contaId"]),
        Index(value = ["categoriaId"]),
        Index(value = ["data"]),
        Index(value = ["tipo"])
    ]
)
data class Evento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val descricao: String,
    val valor: Double,
    val tipo: String, // "RECEITA" ou "DESPESA"
    val data: Long, // timestamp
    val contaId: Long,
    val categoriaId: Long? = null,
    val observacao: String? = null,
    val efetivado: Boolean = true,
    val dataCriacao: Long = System.currentTimeMillis()
)

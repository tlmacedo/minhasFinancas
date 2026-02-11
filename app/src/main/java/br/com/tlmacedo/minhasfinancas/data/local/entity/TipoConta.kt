package br.com.tlmacedo.minhasfinancas.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tipos_conta")
data class TipoConta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val descricao: String? = null,
    @ColumnInfo(defaultValue = "1")
    val ativo: Boolean = true
)

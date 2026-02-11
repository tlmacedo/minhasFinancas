package br.com.tlmacedo.minhasfinancas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val tipo: String, // "RECEITA" ou "DESPESA"
    val icone: String? = "category",
    val cor: String? = "#9E9E9E",
    val ativa: Boolean = true
)

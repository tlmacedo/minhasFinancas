package br.com.tlmacedo.minhasfinancas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val email: String,
    val senhaHash: String, // Hash da senha
    val usarBiometria: Boolean = false,
    val usarReconhecimentoFacial: Boolean = false,
    val fotoPerfilUri: String? = null,
    val ativo: Boolean = true,
    val isAdmin: Boolean = false,
    val dataCriacao: Long = System.currentTimeMillis(),
    val ultimoAcesso: Long? = null
)

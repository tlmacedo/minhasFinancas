package br.com.tlmacedo.minhasfinancas.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration da versão 2 para 3
 * Adiciona o campo bancoId na tabela contas
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE contas ADD COLUMN bancoId TEXT DEFAULT NULL")
    }
}

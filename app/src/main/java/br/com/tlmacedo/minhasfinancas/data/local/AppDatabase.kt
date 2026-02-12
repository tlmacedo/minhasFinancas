package br.com.tlmacedo.minhasfinancas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.tlmacedo.minhasfinancas.data.local.converter.Converters
import br.com.tlmacedo.minhasfinancas.data.local.dao.*
import br.com.tlmacedo.minhasfinancas.data.local.entity.*

@Database(
    entities = [
        Conta::class,
        TipoConta::class,
        Evento::class,
        Categoria::class,
        Usuario::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contaDao(): ContaDao
    abstract fun tipoContaDao(): TipoContaDao
    abstract fun eventoDao(): EventoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun usuarioDao(): UsuarioDao
}

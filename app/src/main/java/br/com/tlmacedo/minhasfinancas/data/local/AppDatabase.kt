package br.com.tlmacedo.minhasfinancas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.tlmacedo.minhasfinancas.data.local.converter.Converters
import br.com.tlmacedo.minhasfinancas.data.local.dao.*
import br.com.tlmacedo.minhasfinancas.data.local.entity.*

/**
 * Ponto de acesso principal ao banco de dados SQLite da aplicação.
 * 
 * Esta classe abstrata define a configuração do banco de dados Room, incluindo
 * a lista de entidades, a versão do esquema e os conversores de tipo necessários.
 * Utiliza o padrão Singleton através do Hilt para garantir uma única instância.
 */
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
    
    /** Fornece acesso aos métodos de manipulação da tabela de Contas */
    abstract fun contaDao(): ContaDao
    
    /** Fornece acesso aos métodos de manipulação da tabela de Tipos de Conta */
    abstract fun tipoContaDao(): TipoContaDao
    
    /** Fornece acesso aos métodos de manipulação da tabela de Eventos (Transações) */
    abstract fun eventoDao(): EventoDao
    
    /** Fornece acesso aos métodos de manipulação da tabela de Categorias */
    abstract fun categoriaDao(): CategoriaDao
    
    /** Fornece acesso aos métodos de manipulação da tabela de Usuários */
    abstract fun usuarioDao(): UsuarioDao
}

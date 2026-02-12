package br.com.tlmacedo.minhasfinancas.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.tlmacedo.minhasfinancas.data.local.AppDatabase
import br.com.tlmacedo.minhasfinancas.data.local.dao.*
import br.com.tlmacedo.minhasfinancas.data.local.migration.MIGRATION_2_3
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Criar tabela de usuários
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    nome TEXT NOT NULL,
                    email TEXT NOT NULL,
                    senhaHash TEXT NOT NULL,
                    usarBiometria INTEGER NOT NULL DEFAULT 0,
                    usarReconhecimentoFacial INTEGER NOT NULL DEFAULT 0,
                    fotoPerfilUri TEXT,
                    ativo INTEGER NOT NULL DEFAULT 1,
                    isAdmin INTEGER NOT NULL DEFAULT 0,
                    dataCriacao INTEGER NOT NULL,
                    ultimoAcesso INTEGER
                )
            """)
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "minhas_financas.db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d("MinhasFinancas", "Database onCreate - inserindo dados iniciais")
                    
                    // Inserir tipos de conta padrão
                    db.execSQL("""
                        INSERT INTO tipos_conta (nome, descricao, ativo) VALUES 
                        ('Conta Corrente', 'Conta corrente bancária', 1),
                        ('Poupança', 'Conta poupança', 1),
                        ('Carteira', 'Dinheiro em espécie', 1),
                        ('Investimento', 'Conta de investimentos', 1),
                        ('Cartão de Crédito', 'Cartão de crédito', 1)
                    """)
                    
                    // Inserir categorias de receita
                    db.execSQL("""
                        INSERT INTO categorias (nome, tipo, icone, cor, ativa) VALUES 
                        ('Salário', 'RECEITA', 'payments', '#4CAF50', 1),
                        ('Freelance', 'RECEITA', 'work', '#8BC34A', 1),
                        ('Investimentos', 'RECEITA', 'trending_up', '#00BCD4', 1),
                        ('Presente', 'RECEITA', 'card_giftcard', '#E91E63', 1),
                        ('Outros', 'RECEITA', 'attach_money', '#9E9E9E', 1)
                    """)
                    
                    // Inserir categorias de despesa
                    db.execSQL("""
                        INSERT INTO categorias (nome, tipo, icone, cor, ativa) VALUES 
                        ('Alimentação', 'DESPESA', 'restaurant', '#FF5722', 1),
                        ('Transporte', 'DESPESA', 'directions_car', '#FF9800', 1),
                        ('Moradia', 'DESPESA', 'home', '#795548', 1),
                        ('Saúde', 'DESPESA', 'local_hospital', '#F44336', 1),
                        ('Educação', 'DESPESA', 'school', '#3F51B5', 1),
                        ('Lazer', 'DESPESA', 'sports_esports', '#E91E63', 1),
                        ('Compras', 'DESPESA', 'shopping_bag', '#9C27B0', 1),
                        ('Contas', 'DESPESA', 'receipt', '#607D8B', 1),
                        ('Outros', 'DESPESA', 'more_horiz', '#9E9E9E', 1)
                    """)
                    
                    Log.d("MinhasFinancas", "Database onCreate - dados iniciais inseridos")
                }
            })
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()
    }

    @Provides
    @Singleton
    fun provideContaDao(database: AppDatabase): ContaDao = database.contaDao()

    @Provides
    @Singleton
    fun provideTipoContaDao(database: AppDatabase): TipoContaDao = database.tipoContaDao()

    @Provides
    @Singleton
    fun provideEventoDao(database: AppDatabase): EventoDao = database.eventoDao()

    @Provides
    @Singleton
    fun provideCategoriaDao(database: AppDatabase): CategoriaDao = database.categoriaDao()

    @Provides
    @Singleton
    fun provideUsuarioDao(database: AppDatabase): UsuarioDao = database.usuarioDao()
}

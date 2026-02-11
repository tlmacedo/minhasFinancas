package br.com.tlmacedo.minhasfinancas.di

import br.com.tlmacedo.minhasfinancas.data.local.dao.*
import br.com.tlmacedo.minhasfinancas.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideContaRepository(contaDao: ContaDao): ContaRepository {
        return ContaRepository(contaDao)
    }

    @Provides
    @Singleton
    fun provideTipoContaRepository(tipoContaDao: TipoContaDao): TipoContaRepository {
        return TipoContaRepository(tipoContaDao)
    }

    @Provides
    @Singleton
    fun provideEventoRepository(eventoDao: EventoDao): EventoRepository {
        return EventoRepository(eventoDao)
    }

    @Provides
    @Singleton
    fun provideCategoriaRepository(categoriaDao: CategoriaDao): CategoriaRepository {
        return CategoriaRepository(categoriaDao)
    }
}

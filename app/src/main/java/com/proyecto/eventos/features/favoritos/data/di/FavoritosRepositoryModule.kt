package com.proyecto.eventos.features.favoritos.data.di

import com.proyecto.eventos.features.favoritos.data.local.FavoritosLocalDataSource
import com.proyecto.eventos.features.favoritos.domain.repositories.FavoritosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoritosRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFavoritosRepository(
        impl: FavoritosLocalDataSource
    ): FavoritosRepository
}

package com.proyecto.eventos.features.panel.data.di

import com.proyecto.eventos.features.panel.data.remote.PanelRemoteDataSource
import com.proyecto.eventos.features.panel.domain.repositories.PanelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PanelRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPanelRepository(
        impl: PanelRemoteDataSource
    ): PanelRepository
}

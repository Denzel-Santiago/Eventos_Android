//com.proyecto.eventos.features.eventos.data.di.EventosRepositoryModule.kt
package com.proyecto.eventos.features.eventos.data.di

import com.proyecto.eventos.features.eventos.data.repository.EventosRepositoryImpl
import com.proyecto.eventos.features.eventos.domain.repositories.EventosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EventosRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEventosRepository(
        impl: EventosRepositoryImpl
    ): EventosRepository
}

//com.proyecto.eventos.features.compras.data.di.ComprasRepositoryModule.kt
package com.proyecto.eventos.features.compras.data.di

import com.proyecto.eventos.features.compras.data.remote.ComprasRemoteDataSource
import com.proyecto.eventos.features.compras.domain.repositories.ComprasRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ComprasRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindComprasRepository(
        impl: ComprasRemoteDataSource
    ): ComprasRepository
}

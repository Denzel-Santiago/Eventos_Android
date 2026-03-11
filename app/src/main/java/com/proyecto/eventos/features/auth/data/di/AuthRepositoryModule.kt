//com.proyecto.eventos.features.auth.data.di.AuthRepositoryModule.kt
package com.proyecto.eventos.features.auth.data.di

import com.proyecto.eventos.features.auth.data.remote.FirebaseAuthDataSource
import com.proyecto.eventos.features.auth.domain.repositories.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        firebaseAuthDataSource: FirebaseAuthDataSource
    ): AuthRepository
}

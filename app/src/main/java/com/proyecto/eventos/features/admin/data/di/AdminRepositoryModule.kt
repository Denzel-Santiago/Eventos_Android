package com.proyecto.eventos.features.admin.data.di

import com.proyecto.eventos.features.admin.data.remote.AdminRemoteDataSource
import com.proyecto.eventos.features.admin.domain.repositories.AdminRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdminRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        impl: AdminRemoteDataSource
    ): AdminRepository
}

package com.proyecto.eventos.core.db.di

import android.content.Context
import androidx.room.Room
import com.proyecto.eventos.core.db.SweepDatabase
import com.proyecto.eventos.features.auth.data.local.UsuarioSesionDao
import com.proyecto.eventos.features.compras.data.local.CompraDao
import com.proyecto.eventos.features.eventos.data.local.EventoDao
import com.proyecto.eventos.features.favoritos.data.local.FavoritoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSweepDatabase(@ApplicationContext context: Context): SweepDatabase {
        return Room.databaseBuilder(
            context,
            SweepDatabase::class.java,
            "sweep_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideFavoritoDao(db: SweepDatabase): FavoritoDao =
        db.favoritoDao()

    @Provides
    @Singleton
    fun provideCompraDao(db: SweepDatabase): CompraDao =
        db.compraDao()

    @Provides
    @Singleton
    fun provideUsuarioSesionDao(db: SweepDatabase): UsuarioSesionDao =
        db.usuarioSesionDao()

    @Provides
    @Singleton
    fun provideEventoDao(db: SweepDatabase): EventoDao =
        db.eventoDao()
}

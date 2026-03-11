package com.proyecto.eventos.core.db.di

import android.content.Context
import androidx.room.Room
import com.proyecto.eventos.core.db.SweepDatabase
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
        ).build()
    }

    @Provides
    @Singleton
    fun provideFavoritoDao(database: SweepDatabase): FavoritoDao {
        return database.favoritoDao()
    }
}

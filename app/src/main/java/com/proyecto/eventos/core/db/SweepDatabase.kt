package com.proyecto.eventos.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proyecto.eventos.features.favoritos.data.local.FavoritoDao
import com.proyecto.eventos.features.favoritos.data.local.FavoritoEntity

@Database(
    entities = [FavoritoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SweepDatabase : RoomDatabase() {
    abstract fun favoritoDao(): FavoritoDao
}

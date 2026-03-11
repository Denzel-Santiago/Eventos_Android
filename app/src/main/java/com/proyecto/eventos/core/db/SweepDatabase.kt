package com.proyecto.eventos.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proyecto.eventos.features.compras.data.local.CompraDao
import com.proyecto.eventos.features.compras.data.local.CompraLocalEntity
import com.proyecto.eventos.features.favoritos.data.local.FavoritoDao
import com.proyecto.eventos.features.favoritos.data.local.FavoritoEntity

@Database(
    entities = [FavoritoEntity::class, CompraLocalEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SweepDatabase : RoomDatabase() {
    abstract fun favoritoDao(): FavoritoDao
    abstract fun compraDao(): CompraDao
}

package com.proyecto.eventos.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proyecto.eventos.features.auth.data.local.UsuarioLocalEntity
import com.proyecto.eventos.features.auth.data.local.UsuarioSesionDao
import com.proyecto.eventos.features.compras.data.local.CompraDao
import com.proyecto.eventos.features.compras.data.local.CompraLocalEntity
import com.proyecto.eventos.features.eventos.data.local.EventoCacheEntity
import com.proyecto.eventos.features.eventos.data.local.EventoDao
import com.proyecto.eventos.features.favoritos.data.local.FavoritoDao
import com.proyecto.eventos.features.favoritos.data.local.FavoritoEntity

@Database(
    entities = [
        FavoritoEntity::class,
        CompraLocalEntity::class,
        UsuarioLocalEntity::class,
        EventoCacheEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class SweepDatabase : RoomDatabase() {
    abstract fun favoritoDao(): FavoritoDao
    abstract fun compraDao(): CompraDao
    abstract fun usuarioSesionDao(): UsuarioSesionDao
    abstract fun eventoDao(): EventoDao
}

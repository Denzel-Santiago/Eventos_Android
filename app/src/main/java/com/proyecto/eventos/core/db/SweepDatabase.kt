package com.proyecto.eventos.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proyecto.eventos.features.auth.data.local.FcmTokenDao
import com.proyecto.eventos.features.auth.data.local.FcmTokenEntity
import com.proyecto.eventos.features.auth.data.local.UsuarioLocalEntity
import com.proyecto.eventos.features.auth.data.local.UsuarioSesionDao
import com.proyecto.eventos.features.compras.data.local.CompraDao
import com.proyecto.eventos.features.compras.data.local.CompraLocalEntity
import com.proyecto.eventos.features.eventos.data.local.EventoCacheEntity
import com.proyecto.eventos.features.eventos.data.local.EventoDao
import com.proyecto.eventos.features.favoritos.data.local.FavoritoDao
import com.proyecto.eventos.features.favoritos.data.local.FavoritoEntity
import com.proyecto.eventos.features.notifications.data.local.NotificationDao
import com.proyecto.eventos.features.notifications.data.local.NotificationEntity

@Database(
    entities = [
        FavoritoEntity::class,
        CompraLocalEntity::class,
        UsuarioLocalEntity::class,
        EventoCacheEntity::class,
        FcmTokenEntity::class,
        NotificationEntity::class
    ],
    version = 11, // Incrementado de 10 a 11 debido al cambio en NotificationEntity
    exportSchema = false
)
abstract class SweepDatabase : RoomDatabase() {
    abstract fun favoritoDao(): FavoritoDao
    abstract fun compraDao(): CompraDao
    abstract fun usuarioSesionDao(): UsuarioSesionDao
    abstract fun eventoDao(): EventoDao
    abstract fun fcmTokenDao(): FcmTokenDao
    abstract fun notificationDao(): NotificationDao
}
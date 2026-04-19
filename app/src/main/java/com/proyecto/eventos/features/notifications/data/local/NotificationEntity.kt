//com.proyecto.eventos.features.notifications.data.local.NotificationEntity.kt
package com.proyecto.eventos.features.notifications.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notificaciones")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val userId: String, // Agregado para filtrar por usuario
    val titulo: String,
    val cuerpo: String,
    val timestamp: Long = System.currentTimeMillis(),
    val leida: Boolean = false
)
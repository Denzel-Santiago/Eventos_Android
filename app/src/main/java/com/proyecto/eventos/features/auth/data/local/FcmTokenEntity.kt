//com.proyecto.eventos.features.auth.data.local.FcmTokenEntity.kt
package com.proyecto.eventos.features.auth.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fcm_tokens")
data class FcmTokenEntity(
    @PrimaryKey
    val uid: String,
    val token: String,
    val ultimaActualizacion: Long = System.currentTimeMillis()
)
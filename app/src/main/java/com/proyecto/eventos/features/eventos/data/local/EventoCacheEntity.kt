//com.proyecto.eventos.features.eventos.data.local.EventoCacheEntity.kt
package com.proyecto.eventos.features.eventos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eventos_cache")
data class EventoCacheEntity(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val fecha: String,
    val hora: String,
    val ubicacion: String,
    val precio: Double,
    val stock: Int,
    val imagen: String
)

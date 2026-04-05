//com.proyecto.eventos.features.auth.data.local.UsuarioLocalEntity.kt
package com.proyecto.eventos.features.auth.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario_sesion")
data class UsuarioLocalEntity(
    @PrimaryKey
    val uid: String,
    val nombre: String,
    val email: String,
    val rol: String,
    val ultimaActualizacion: Long = System.currentTimeMillis()
)

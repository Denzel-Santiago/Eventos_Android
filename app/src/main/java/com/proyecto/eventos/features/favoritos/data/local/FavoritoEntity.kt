//com.proyecto.eventos.features.favoritos.data.local.FavoritoEntity
package com.proyecto.eventos.features.favoritos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoritos")
data class FavoritoEntity(
    @PrimaryKey
    val id: String,
    val uid: String,
    val eventoId: String,
    val nombre: String,
    val fecha: String,
    val hora: String,
    val ubicacion: String,
    val precio: Double,
    val stock: Int,
    val imagen: String
)
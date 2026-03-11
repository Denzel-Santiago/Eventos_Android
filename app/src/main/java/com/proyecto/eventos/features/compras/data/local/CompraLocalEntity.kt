package com.proyecto.eventos.features.compras.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_compras")
data class CompraLocalEntity(
    @PrimaryKey
    val id: String,
    val eventoId: String,
    val nombreEvento: String,
    val fecha: String,
    val hora: String,
    val precio: Double,
    val direccionEntrega: String,
    val fotoInePath: String,
    val timestamp: Long
)

package com.proyecto.eventos.features.compras.domain.entities

data class CompraEntidad(
    val id: String = "",
    val eventoId: String = "",
    val nombreEvento: String = "",
    val fecha: String = "",
    val hora: String = "",
    val precio: Double = 0.0,
    val direccionEntrega: String = "",
    val fotoInePath: String = "",
    val timestamp: Long = 0L
)

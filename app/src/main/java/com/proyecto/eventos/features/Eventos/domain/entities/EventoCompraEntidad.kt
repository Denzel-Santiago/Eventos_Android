package com.proyecto.eventos.features.Eventos.domain.entities

data class EventoCompraEntidad(
    val nombre: String,
    val ubicacion: String,
    val fecha: String,
    val boletosDisponibles: Int,
    val precio: Double
)

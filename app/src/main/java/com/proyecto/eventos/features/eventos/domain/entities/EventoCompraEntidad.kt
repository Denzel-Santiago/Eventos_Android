//features/Eventos/domain/entities/EventoCompraEntidad.kt
package com.proyecto.eventos.features.eventos.domain.entities

data class EventoCompraEntidad(
    val nombre: String,
    val ubicacion: String,
    val fecha: String,
    val boletosDisponibles: Int,
    val precio: Double
)

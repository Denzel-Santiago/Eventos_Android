//features/Eventos/domain/entities/EventoEntidad.kt
package com.proyecto.eventos.features.eventos.domain.entities

data class EventoEntidad(
    val id: Int,
    val nombre: String,
    val ubicacion: String,
    val fecha: String,
    val boletosDisponibles: Int,
    val precio: Double
)
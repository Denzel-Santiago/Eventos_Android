//features/Eventos/domain/entities/EventoAdminEntidad.kt
package com.proyecto.eventos.features.eventos.domain.entities

data class EventoAdminEntidad(
    val id: Int,
    var nombre: String,
    var ubicacion: String,
    var fecha: String,
    var precio: Double,
    var boletosDisponibles: Int
)

package com.proyecto.eventos.features.Eventos.domain.entities

data class EventoAdminEntidad(
    val id: Int,
    var nombre: String,
    var ubicacion: String,
    var fecha: String,
    var precio: Double,
    var boletosDisponibles: Int
)

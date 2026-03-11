//com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad.kt
package com.proyecto.eventos.features.eventos.domain.entities

data class EventoEntidad(
    val id: String = "",
    val nombre: String = "",
    val fecha: String = "",
    val hora: String = "",
    val ubicacion: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val imagen: String = ""
)
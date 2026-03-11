//com.proyecto.eventos.features.eventos.data.remote.EventoDTO.kt
package com.proyecto.eventos.features.eventos.data.remote

data class EventoDTO(
    val id: String = "",
    val nombre: String = "",
    val fecha: String = "",
    val hora: String = "",
    val ubicacion: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val imagen: String = ""
)

package com.proyecto.eventos.features.favoritos.domain.entities

data class FavoritoEntidad(
    val eventoId: String = "",
    val nombre: String = "",
    val fecha: String = "",
    val hora: String = "",
    val ubicacion: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val imagen: String = ""
)

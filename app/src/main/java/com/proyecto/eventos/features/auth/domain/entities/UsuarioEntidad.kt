//com.proyecto.eventos.features.auth.domain.entities.UsuarioEntidad.kt
package com.proyecto.eventos.features.auth.domain.entities

data class UsuarioEntidad(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val rol: String = "usuario"
)

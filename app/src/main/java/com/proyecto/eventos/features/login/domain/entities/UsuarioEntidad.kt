//features/login/domain/entities/UsuarioEntidad.kt
package com.proyecto.eventos.features.login.domain.entities

data class UsuarioEntidad(
    val id: Int,
    val username: String,
    val email: String,
    val role: String
)
package com.proyecto.eventos.features.admin.domain.entities

data class UsuarioAdminEntidad(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val rol: String = "usuario"
)

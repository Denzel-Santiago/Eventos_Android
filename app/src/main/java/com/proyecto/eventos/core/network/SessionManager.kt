package com.proyecto.eventos.core.network

import com.proyecto.eventos.features.login.domain.entities.UsuarioEntidad

object SessionManager {
    private var usuarioActual: UsuarioEntidad? = null

    fun iniciarSesion(usuario: UsuarioEntidad) {
        usuarioActual = usuario
    }

    fun cerrarSesion() {
        usuarioActual = null
    }

    fun obtenerUsuario(): UsuarioEntidad? {
        return usuarioActual
    }

    fun estaAutenticado(): Boolean {
        return usuarioActual != null
    }

    fun esAdmin(): Boolean {
        return usuarioActual?.role == "admin"
    }
}
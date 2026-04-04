package com.proyecto.eventos.features.auth.data.local

import com.proyecto.eventos.features.auth.domain.entities.UsuarioEntidad
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(
    private val dao: UsuarioSesionDao
) {
    suspend fun guardarSesion(usuario: UsuarioEntidad) {
        dao.guardarSesion(
            UsuarioLocalEntity(
                uid = usuario.uid,
                nombre = usuario.nombre,
                email = usuario.email,
                rol = usuario.rol
            )
        )
    }

    suspend fun getSesionGuardada(): UsuarioEntidad? {
        val local = dao.getSesionActual() ?: return null
        return UsuarioEntidad(
            uid = local.uid,
            nombre = local.nombre,
            email = local.email,
            rol = local.rol
        )
    }

    suspend fun cerrarSesion() {
        dao.cerrarSesion()
    }
}

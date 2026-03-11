//com.proyecto.eventos.features.auth.domain.repositories.AuthRepository.kt
package com.proyecto.eventos.features.auth.domain.repositories

import com.proyecto.eventos.features.auth.domain.entities.UsuarioEntidad

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<UsuarioEntidad>
    suspend fun registro(nombre: String, email: String, password: String): Result<UsuarioEntidad>
    suspend fun logout()
    fun getUsuarioActual(): UsuarioEntidad?
    fun estaAutenticado(): Boolean
    fun esAdmin(): Boolean
}

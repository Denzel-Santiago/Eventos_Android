package com.proyecto.eventos.features.login.data.repositories

import com.proyecto.eventos.core.network.RetrofitClient
import com.proyecto.eventos.features.login.data.remote.LoginRequest
import com.proyecto.eventos.features.login.data.remote.RegistroRequest
import com.proyecto.eventos.features.login.data.remote.toEntity
import com.proyecto.eventos.features.login.domain.entities.UsuarioEntidad

class AuthRepository {

    private val api = RetrofitClient.authApi

    suspend fun login(username: String, password: String): UsuarioEntidad? {
        return try {
            val request = LoginRequest(username, password)
            val response = api.login(request)
            response.user.toEntity()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun registro(username: String, email: String, password: String): UsuarioEntidad? {
        return try {
            val request = RegistroRequest(username, email, password, "user")
            val response = api.registro(request)
            response.toEntity()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun eliminarUsuario(id: Int): Boolean {
        return try {
            api.deleteUsuario(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}
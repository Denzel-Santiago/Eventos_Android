package com.proyecto.eventos.features.login.domain.usecases

import com.proyecto.eventos.features.login.data.repositories.AuthRepository
import com.proyecto.eventos.features.login.domain.entities.UsuarioEntidad

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend fun execute(username: String, password: String): UsuarioEntidad? {
        return repository.login(username, password)
    }
}
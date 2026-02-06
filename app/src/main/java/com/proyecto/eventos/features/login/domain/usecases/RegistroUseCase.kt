//features/login/domain/usecases/LoginUseCase.kt
package com.proyecto.eventos.features.login.domain.usecases

import com.proyecto.eventos.features.login.data.repositories.AuthRepository
import com.proyecto.eventos.features.login.domain.entities.UsuarioEntidad

class RegistroUseCase(
    private val repository: AuthRepository
) {
    suspend fun execute(username: String, email: String, password: String): UsuarioEntidad? {
        return repository.registro(username, email, password)
    }
}
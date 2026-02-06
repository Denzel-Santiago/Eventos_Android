//features/login/domain/usecases/UpdateUsuarioUseCase.kt
package com.proyecto.eventos.features.login.domain.usecases

import com.proyecto.eventos.features.login.data.repositories.AuthRepository

class UpdateUsuarioUseCase(
    private val repository: AuthRepository
) {
    suspend fun execute(id: Int, username: String, email: String): Boolean {
        return repository.actualizarUsuario(id, username, email)
    }
}
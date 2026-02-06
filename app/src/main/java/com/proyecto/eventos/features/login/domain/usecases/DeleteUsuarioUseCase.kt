//features/login/domain/usecases/DeleteUsuarioUseCase.kt
package com.proyecto.eventos.features.login.domain.usecases

import com.proyecto.eventos.features.login.data.repositories.AuthRepository

class DeleteUsuarioUseCase(
    private val repository: AuthRepository
) {
    suspend fun execute(id: Int): Boolean {
        return repository.eliminarUsuario(id)
    }
}
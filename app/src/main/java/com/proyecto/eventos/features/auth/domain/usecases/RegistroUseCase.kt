//com.proyecto.eventos.features.auth.domain.usecases.RegistroUseCase.kt
package com.proyecto.eventos.features.auth.domain.usecases

import com.proyecto.eventos.features.auth.domain.entities.UsuarioEntidad
import com.proyecto.eventos.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class RegistroUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        nombre: String,
        email: String,
        password: String
    ): Result<UsuarioEntidad> {
        return repository.registro(nombre, email, password)
    }
}

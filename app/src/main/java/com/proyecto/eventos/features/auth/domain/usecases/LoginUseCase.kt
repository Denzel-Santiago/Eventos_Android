//com.proyecto.eventos.features.auth.domain.usecases.LoginUseCase.kt
package com.proyecto.eventos.features.auth.domain.usecases

import com.proyecto.eventos.features.auth.domain.entities.UsuarioEntidad
import com.proyecto.eventos.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UsuarioEntidad> {
        return repository.login(email, password)
    }
}

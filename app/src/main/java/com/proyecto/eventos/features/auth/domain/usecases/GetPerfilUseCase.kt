//com.proyecto.eventos.features.auth.domain.usecases.GetPerfilUseCase.kt
package com.proyecto.eventos.features.auth.domain.usecases

import com.proyecto.eventos.features.auth.domain.entities.UsuarioEntidad
import com.proyecto.eventos.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class GetPerfilUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): UsuarioEntidad? {
        return repository.getUsuarioActual()
    }
}

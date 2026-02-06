//features/login/domain/usecases/GetUsuariosUseCase.kt
package com.proyecto.eventos.features.login.domain.usecases

import com.proyecto.eventos.features.login.data.repositories.AuthRepository
import com.proyecto.eventos.features.login.domain.entities.UsuarioEntidad

class GetUsuariosUseCase(
    private val repository: AuthRepository
) {
    suspend fun execute(): List<UsuarioEntidad> {
        return repository.obtenerUsuarios()
    }
}
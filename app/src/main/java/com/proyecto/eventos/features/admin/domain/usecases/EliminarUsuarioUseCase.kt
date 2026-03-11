package com.proyecto.eventos.features.admin.domain.usecases

import com.proyecto.eventos.features.admin.domain.repositories.AdminRepository
import javax.inject.Inject

class EliminarUsuarioUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(uid: String): Result<Unit> = repository.eliminarUsuario(uid)
}

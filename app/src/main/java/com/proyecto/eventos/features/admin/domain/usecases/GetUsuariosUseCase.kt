package com.proyecto.eventos.features.admin.domain.usecases

import com.proyecto.eventos.features.admin.domain.entities.UsuarioAdminEntidad
import com.proyecto.eventos.features.admin.domain.repositories.AdminRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsuariosUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    operator fun invoke(): Flow<List<UsuarioAdminEntidad>> = repository.getUsuarios()
}

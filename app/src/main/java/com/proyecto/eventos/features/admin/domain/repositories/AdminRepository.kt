//com.proyecto.eventos.features.admin.domain.repositories.AdminRepository.kt
package com.proyecto.eventos.features.admin.domain.repositories

import com.proyecto.eventos.features.admin.domain.entities.UsuarioAdminEntidad
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getUsuarios(): Flow<List<UsuarioAdminEntidad>>
    suspend fun eliminarUsuario(uid: String): Result<Unit>
}

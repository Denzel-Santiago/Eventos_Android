//com.proyecto.eventos.features.panel.domain.repositories.PanelRepository.kt
package com.proyecto.eventos.features.panel.domain.repositories

import com.proyecto.eventos.features.auth.domain.entities.UsuarioEntidad

interface PanelRepository {
    suspend fun getUsuarioActual(): UsuarioEntidad?
    suspend fun logout()
    fun estaAutenticado(): Boolean
}
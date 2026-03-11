package com.proyecto.eventos.features.panel.domain.repositories

import com.proyecto.eventos.features.auth.domain.entities.UsuarioEntidad

interface PanelRepository {
    suspend fun getUsuarioActual(): UsuarioEntidad?
    fun logout()
    fun estaAutenticado(): Boolean
}

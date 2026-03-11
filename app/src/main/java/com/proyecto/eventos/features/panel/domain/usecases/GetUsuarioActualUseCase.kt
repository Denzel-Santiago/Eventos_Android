package com.proyecto.eventos.features.panel.domain.usecases

import com.proyecto.eventos.features.auth.domain.entities.UsuarioEntidad
import com.proyecto.eventos.features.panel.domain.repositories.PanelRepository
import javax.inject.Inject

class GetUsuarioActualUseCase @Inject constructor(
    private val repository: PanelRepository
) {
    suspend operator fun invoke(): UsuarioEntidad? = repository.getUsuarioActual()
}

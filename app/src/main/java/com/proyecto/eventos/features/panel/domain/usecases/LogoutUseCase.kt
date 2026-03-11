package com.proyecto.eventos.features.panel.domain.usecases

import com.proyecto.eventos.features.panel.domain.repositories.PanelRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: PanelRepository
) {
    operator fun invoke() = repository.logout()
}

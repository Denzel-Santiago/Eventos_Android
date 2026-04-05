//com.proyecto.eventos.features.panel.domain.usecases.LogoutUseCase.kt
package com.proyecto.eventos.features.panel.domain.usecases

import com.proyecto.eventos.features.auth.data.local.AuthLocalDataSource
import com.proyecto.eventos.features.panel.domain.repositories.PanelRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: PanelRepository,
    private val authLocalDataSource: AuthLocalDataSource
) {
    suspend operator fun invoke() {
        repository.logout()
        authLocalDataSource.cerrarSesion()
    }
}
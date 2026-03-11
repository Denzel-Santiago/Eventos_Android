//com.proyecto.eventos.features.eventos.domain.usecases.DeleteEventoUseCase.kt
package com.proyecto.eventos.features.eventos.domain.usecases

import com.proyecto.eventos.features.eventos.domain.repositories.EventosRepository
import javax.inject.Inject

class DeleteEventoUseCase @Inject constructor(
    private val repository: EventosRepository
) {
    suspend operator fun invoke(eventoId: String): Result<Unit> {
        return repository.deleteEvento(eventoId)
    }
}

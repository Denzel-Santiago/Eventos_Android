//features/Eventos/domain/usecases/UpdateEventoUseCase.kt
package com.proyecto.eventos.features.eventos.domain.usecases

import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.eventos.domain.repositories.EventosRepository
import javax.inject.Inject

class UpdateEventoUseCase @Inject constructor(
    private val repository: EventosRepository
) {
    suspend operator fun invoke(evento: EventoEntidad): Result<Unit> {
        return repository.updateEvento(evento)
    }
}

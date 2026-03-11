//com.proyecto.eventos.features.eventos.domain.usecases.CreateEventoUseCase.kt
package com.proyecto.eventos.features.eventos.domain.usecases

import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.eventos.domain.repositories.EventosRepository
import javax.inject.Inject

class CreateEventoUseCase @Inject constructor(
    private val repository: EventosRepository
) {
    suspend operator fun invoke(evento: EventoEntidad): Result<Unit> {
        return repository.createEvento(evento)
    }
}

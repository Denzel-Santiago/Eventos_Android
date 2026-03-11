//com.proyecto.eventos.features.eventos.domain.usecases.GetEventosUseCase.kt
package com.proyecto.eventos.features.eventos.domain.usecases

import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.eventos.domain.repositories.EventosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventosUseCase @Inject constructor(
    private val repository: EventosRepository
) {
    operator fun invoke(): Flow<List<EventoEntidad>> {
        return repository.getEventos()
    }
}

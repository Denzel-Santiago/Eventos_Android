//features/Eventos/domain/usecases/GetEventosUseCase.kt
package com.proyecto.eventos.features.eventos.domain.usecases

import com.proyecto.eventos.features.eventos.data.repositories.EventosRepository
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad

class GetEventosUseCase(
    private val repository: EventosRepository
) {
    suspend fun execute(): List<EventoEntidad> {
        return repository.obtenerEventos()
    }
}
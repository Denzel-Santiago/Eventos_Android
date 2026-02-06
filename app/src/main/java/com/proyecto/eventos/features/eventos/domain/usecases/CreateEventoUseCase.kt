//features/Eventos/domain/usecases/CreateEventoUseCase.kt
package com.proyecto.eventos.features.eventos.domain.usecases

import com.proyecto.eventos.features.eventos.data.repositories.EventosRepository
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad

class CreateEventoUseCase(
    private val repository: EventosRepository
) {
    suspend fun execute(evento: EventoEntidad): Boolean {
        return repository.crearEvento(evento)
    }
}
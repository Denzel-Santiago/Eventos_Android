//features/Eventos/domain/usecases/UpdateEventoUseCase.kt
package com.proyecto.eventos.features.eventos.domain.usecases

import com.proyecto.eventos.features.eventos.data.repositories.EventosRepository
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad

class UpdateEventoUseCase(
    private val repository: EventosRepository
) {
    suspend fun execute(evento: EventoEntidad): Boolean {
        return repository.actualizarEvento(evento)
    }
}
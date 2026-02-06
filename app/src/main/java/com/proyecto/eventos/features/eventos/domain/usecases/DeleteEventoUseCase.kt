//features/Eventos/domain/usecases/DeleteEventoUseCase.kt
package com.proyecto.eventos.features.eventos.domain.usecases

import com.proyecto.eventos.features.eventos.data.repositories.EventosRepository

class DeleteEventoUseCase(
    private val repository: EventosRepository
) {
    suspend fun execute(id: Int): Boolean {
        return repository.eliminarEvento(id)
    }
}
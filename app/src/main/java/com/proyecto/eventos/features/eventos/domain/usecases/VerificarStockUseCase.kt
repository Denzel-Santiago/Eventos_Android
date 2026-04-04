package com.proyecto.eventos.features.eventos.domain.usecases

import com.proyecto.eventos.features.eventos.domain.repositories.EventosRepository
import javax.inject.Inject

class VerificarStockUseCase @Inject constructor(
    private val repository: EventosRepository
) {
    suspend operator fun invoke(eventoId: String): Result<Boolean> {
        return repository.tieneStock(eventoId)
    }
}

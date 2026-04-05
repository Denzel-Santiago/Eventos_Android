//com.proyecto.eventos.features.eventos.domain.usecases.RestarStockUseCase.kt
package com.proyecto.eventos.features.eventos.domain.usecases

import com.proyecto.eventos.features.eventos.domain.repositories.EventosRepository
import javax.inject.Inject

class RestarStockUseCase @Inject constructor(
    private val repository: EventosRepository
) {
    suspend operator fun invoke(eventoId: String): Result<Unit> {
        return repository.restarStock(eventoId)
    }
}

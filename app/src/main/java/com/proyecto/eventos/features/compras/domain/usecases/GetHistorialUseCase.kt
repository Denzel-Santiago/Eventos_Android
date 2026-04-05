//com.proyecto.eventos.features.compras.domain.usecases.GetHistorialUseCase.kt
package com.proyecto.eventos.features.compras.domain.usecases

import com.proyecto.eventos.features.compras.domain.entities.CompraEntidad
import com.proyecto.eventos.features.compras.domain.repositories.ComprasRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHistorialUseCase @Inject constructor(
    private val repository: ComprasRepository
) {
    operator fun invoke(uid: String): Flow<List<CompraEntidad>> = repository.getHistorial(uid)
}

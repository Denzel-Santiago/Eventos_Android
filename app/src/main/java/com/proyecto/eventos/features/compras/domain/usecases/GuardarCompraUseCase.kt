package com.proyecto.eventos.features.compras.domain.usecases

import com.proyecto.eventos.features.compras.domain.entities.CompraEntidad
import com.proyecto.eventos.features.compras.domain.repositories.ComprasRepository
import javax.inject.Inject

class GuardarCompraUseCase @Inject constructor(
    private val repository: ComprasRepository
) {
    suspend operator fun invoke(uid: String, compra: CompraEntidad): Result<Unit> {
        return repository.guardarCompra(uid, compra)
    }
}

package com.proyecto.eventos.features.compras.domain.repositories

import com.proyecto.eventos.features.compras.domain.entities.CompraEntidad
import kotlinx.coroutines.flow.Flow

interface ComprasRepository {
    suspend fun guardarCompra(uid: String, compra: CompraEntidad): Result<Unit>
    fun getHistorial(): Flow<List<CompraEntidad>>
}

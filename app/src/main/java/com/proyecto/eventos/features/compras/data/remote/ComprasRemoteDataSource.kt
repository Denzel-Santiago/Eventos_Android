package com.proyecto.eventos.features.compras.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.proyecto.eventos.features.compras.data.local.CompraDao
import com.proyecto.eventos.features.compras.data.local.CompraLocalEntity
import com.proyecto.eventos.features.compras.domain.entities.CompraEntidad
import com.proyecto.eventos.features.compras.domain.repositories.ComprasRepository
import com.proyecto.eventos.core.network.NetworkMonitor
import com.proyecto.eventos.features.eventos.domain.usecases.RestarStockUseCase
import com.proyecto.eventos.features.eventos.domain.usecases.VerificarStockUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ComprasRemoteDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val compraDao: CompraDao,
    private val networkMonitor: NetworkMonitor,
    private val restarStockUseCase: RestarStockUseCase,
    private val verificarStockUseCase: VerificarStockUseCase
) : ComprasRepository {

    override suspend fun guardarCompra(uid: String, compra: CompraEntidad): Result<Unit> {
        return try {
            // 1. Verificar stock antes de proceder
            if (networkMonitor.isConnected()) {
                val tieneStock = verificarStockUseCase(compra.eventoId)
                    .getOrElse { return Result.failure(it) }
                if (!tieneStock) {
                    return Result.failure(Exception("No hay boletos disponibles"))
                }
            }

            val compraId = UUID.randomUUID().toString()

            // 2. PRIMERO guardar en Room (siempre)
            compraDao.insertar(
                CompraLocalEntity(
                    id = compraId,
                    uid = uid,
                    eventoId = compra.eventoId,
                    nombreEvento = compra.nombreEvento,
                    fecha = compra.fecha,
                    hora = compra.hora,
                    precio = compra.precio,
                    direccionEntrega = compra.direccionEntrega,
                    fotoInePath = compra.fotoInePath,
                    timestamp = compra.timestamp
                )
            )

            // 3. Si hay internet, sincronizar compra con Firebase
            if (networkMonitor.isConnected()) {
                val data = mapOf(
                    "eventoId" to compra.eventoId,
                    "nombreEvento" to compra.nombreEvento,
                    "fecha" to compra.fecha,
                    "hora" to compra.hora,
                    "precio" to compra.precio,
                    "direccionEntrega" to compra.direccionEntrega,
                    "fotoInePath" to compra.fotoInePath,
                    "timestamp" to compra.timestamp
                )
                firebaseDatabase
                    .getReference("compras")
                    .child(uid)
                    .child(compraId)
                    .setValue(data)
                    .await()

                // 4. Restar stock de forma atómica en Firebase
                restarStockUseCase(compra.eventoId)
                    .getOrElse { return Result.failure(it) }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getHistorial(uid: String): Flow<List<CompraEntidad>> {
        return compraDao.getHistorial(uid).map { lista ->
            lista.map { entity ->
                CompraEntidad(
                    id = entity.id,
                    eventoId = entity.eventoId,
                    nombreEvento = entity.nombreEvento,
                    fecha = entity.fecha,
                    hora = entity.hora,
                    precio = entity.precio,
                    direccionEntrega = entity.direccionEntrega,
                    fotoInePath = entity.fotoInePath,
                    timestamp = entity.timestamp
                )
            }
        }
    }
}

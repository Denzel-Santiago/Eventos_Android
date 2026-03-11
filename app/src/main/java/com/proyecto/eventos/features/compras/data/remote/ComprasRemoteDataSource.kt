package com.proyecto.eventos.features.compras.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.proyecto.eventos.features.compras.data.local.CompraDao
import com.proyecto.eventos.features.compras.data.local.CompraLocalEntity
import com.proyecto.eventos.features.compras.domain.entities.CompraEntidad
import com.proyecto.eventos.features.compras.domain.repositories.ComprasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ComprasRemoteDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val compraDao: CompraDao
) : ComprasRepository {

    override suspend fun guardarCompra(uid: String, compra: CompraEntidad): Result<Unit> {
        return try {
            val compraId = UUID.randomUUID().toString()

            val compraData = mapOf(
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
                .setValue(compraData)
                .await()

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

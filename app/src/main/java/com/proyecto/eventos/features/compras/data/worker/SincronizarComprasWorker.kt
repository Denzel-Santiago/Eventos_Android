package com.proyecto.eventos.features.compras.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.database.FirebaseDatabase
import com.proyecto.eventos.features.compras.data.local.CompraDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

@HiltWorker
class SincronizarComprasWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val compraDao: CompraDao,
    private val firebaseDatabase: FirebaseDatabase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val uid = inputData.getString("uid") ?: return Result.failure()

            // Obtener compras locales y sincronizar con Firebase
            val compras = compraDao.getHistorial(uid).first()

            compras.forEach { compra ->
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
                    .child(compra.id)
                    .setValue(data)
                    .await()
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
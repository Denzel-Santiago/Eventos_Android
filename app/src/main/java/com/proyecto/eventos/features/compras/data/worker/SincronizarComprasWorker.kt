package com.proyecto.eventos.features.compras.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.database.FirebaseDatabase
import com.proyecto.eventos.features.compras.data.local.CompraDao
import com.proyecto.eventos.features.eventos.domain.usecases.RestarStockUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class SincronizarComprasWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val compraDao: CompraDao,
    private val firebaseDatabase: FirebaseDatabase,
    private val restarStockUseCase: RestarStockUseCase
) : CoroutineWorker(context, params) {

    companion object {
        const val OFFLINE_NOTIFICATION_ID = 2002
        const val CHANNEL_ID = "compra_foreground_channel"
    }

    override suspend fun doWork(): Result {
        val uid = inputData.getString("uid") ?: return Result.failure()

        return try {
            val comprasPendientes = compraDao.getComprasPendientesPorUsuario(uid)

            if (comprasPendientes.isEmpty()) return Result.success()

            comprasPendientes.forEach { compra ->
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

                restarStockUseCase(compra.eventoId)
                compraDao.marcarSincronizada(compra.id)
            }

            // Eliminar la notificación de "Compra guardada offline" y mostrar éxito
            finalizarNotificaciones()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun finalizarNotificaciones() {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // 1. Quitar la notificación persistente de offline
        nm.cancel(OFFLINE_NOTIFICATION_ID)

        // 2. Mostrar notificación de éxito en la sincronización
        val successNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡Sincronización completa! 🎫")
            .setContentText("Tus compras pendientes se han procesado con éxito")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        nm.notify(System.currentTimeMillis().toInt(), successNotification)
    }
}

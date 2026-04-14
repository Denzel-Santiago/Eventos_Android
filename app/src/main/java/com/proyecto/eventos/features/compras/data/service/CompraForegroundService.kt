package com.proyecto.eventos.features.compras.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.proyecto.eventos.features.auth.data.local.FcmTokenDao
import com.proyecto.eventos.features.compras.data.local.CompraDao
import com.proyecto.eventos.features.compras.data.local.CompraLocalEntity
import com.proyecto.eventos.features.compras.data.worker.SincronizarComprasWorker
import com.proyecto.eventos.features.notifications.data.remote.FcmApiService
import com.proyecto.eventos.features.eventos.domain.usecases.RestarStockUseCase
import com.proyecto.eventos.core.network.NetworkMonitor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlinx.coroutines.delay

@AndroidEntryPoint
class CompraForegroundService : Service() {

    @Inject lateinit var firebaseDatabase: FirebaseDatabase
    @Inject lateinit var compraDao: CompraDao
    @Inject lateinit var fcmApiService: FcmApiService
    @Inject lateinit var fcmTokenDao: FcmTokenDao
    @Inject lateinit var firebaseAuth: FirebaseAuth
    @Inject lateinit var restarStockUseCase: RestarStockUseCase
    @Inject lateinit var networkMonitor: NetworkMonitor

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val CHANNEL_ID = "compra_foreground_channel"
        const val NOTIFICATION_ID = 1001
        const val OFFLINE_NOTIFICATION_ID = 2002 // ID fijo para poder cancelarla luego
        const val EXTRA_UID = "uid"
        const val EXTRA_COMPRA_ID = "compra_id"
        const val EXTRA_EVENTO_ID = "evento_id"
        const val EXTRA_NOMBRE_EVENTO = "nombre_evento"
        const val EXTRA_FECHA = "fecha"
        const val EXTRA_HORA = "hora"
        const val EXTRA_PRECIO = "precio"
        const val EXTRA_DIRECCION = "direccion"
        const val EXTRA_FOTO_PATH = "foto_path"
        const val EXTRA_TIMESTAMP = "timestamp"

        fun buildIntent(context: Context, uid: String, compraId: String, eventoId: String, nombreEvento: String, fecha: String, hora: String, precio: Double, direccion: String, fotoPath: String, timestamp: Long): Intent {
            return Intent(context, CompraForegroundService::class.java).apply {
                putExtra(EXTRA_UID, uid)
                putExtra(EXTRA_COMPRA_ID, compraId)
                putExtra(EXTRA_EVENTO_ID, eventoId)
                putExtra(EXTRA_NOMBRE_EVENTO, nombreEvento)
                putExtra(EXTRA_FECHA, fecha)
                putExtra(EXTRA_HORA, hora)
                putExtra(EXTRA_PRECIO, precio)
                putExtra(EXTRA_DIRECCION, direccion)
                putExtra(EXTRA_FOTO_PATH, fotoPath)
                putExtra(EXTRA_TIMESTAMP, timestamp)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        crearCanal()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val uid = intent?.getStringExtra(EXTRA_UID) ?: return START_NOT_STICKY
        val compraId = intent.getStringExtra(EXTRA_COMPRA_ID) ?: return START_NOT_STICKY
        val eventoId = intent.getStringExtra(EXTRA_EVENTO_ID) ?: return START_NOT_STICKY
        val nombreEvento = intent.getStringExtra(EXTRA_NOMBRE_EVENTO) ?: return START_NOT_STICKY
        val fecha = intent.getStringExtra(EXTRA_FECHA) ?: ""
        val hora = intent.getStringExtra(EXTRA_HORA) ?: ""
        val precio = intent.getDoubleExtra(EXTRA_PRECIO, 0.0)
        val direccion = intent.getStringExtra(EXTRA_DIRECCION) ?: ""
        val fotoPath = intent.getStringExtra(EXTRA_FOTO_PATH) ?: ""
        val timestamp = intent.getLongExtra(EXTRA_TIMESTAMP, System.currentTimeMillis())

        startForeground(NOTIFICATION_ID, buildNotificacion("Procesando tu compra..."))

        serviceScope.launch {
            procesarCompra(uid, compraId, eventoId, nombreEvento, fecha, hora, precio, direccion, fotoPath, timestamp)
        }
        return START_NOT_STICKY
    }

    private suspend fun procesarCompra(uid: String, compraId: String, eventoId: String, nombreEvento: String, fecha: String, hora: String, precio: Double, direccion: String, fotoPath: String, timestamp: Long) {
        delay(10000L)
        try {
            compraDao.insertar(CompraLocalEntity(compraId, uid, eventoId, nombreEvento, fecha, hora, precio, direccion, fotoPath, timestamp, false))

            if (networkMonitor.isConnected()) {
                subirAFirebaseYRestarStock(uid, compraId, eventoId, nombreEvento, fecha, hora, precio, direccion, fotoPath, timestamp)
                val tokenEntity = fcmTokenDao.getToken(uid)
                if (tokenEntity != null) {
                    fcmApiService.enviarNotificacion(tokenEntity.token, "¡Compra confirmada! 🎫", "Tu boleto para $nombreEvento ha sido procesado", eventoId, nombreEvento)
                }
                mostrarNotificacionFinal(System.currentTimeMillis().toInt(), "¡Compra exitosa! 🎫", "Tu boleto para $nombreEvento ha sido confirmado")
            } else {
                programarSincronizacionWorkManager(uid)
                // Usamos ID FIJO para la notificación offline
                mostrarNotificacionFinal(OFFLINE_NOTIFICATION_ID, "Compra guardada (Offline) 📥", "Se sincronizará al detectar internet.")
            }
        } catch (e: Exception) {
            programarSincronizacionWorkManager(uid)
            mostrarNotificacionFinal(OFFLINE_NOTIFICATION_ID, "Procesando en segundo plano", "Tu compra se completará en breve.")
        } finally {
            stopSelf()
        }
    }

    private fun programarSincronizacionWorkManager(uid: String) {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val syncRequest = OneTimeWorkRequestBuilder<SincronizarComprasWorker>()
            .setConstraints(constraints)
            .setInputData(workDataOf("uid" to uid))
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, java.util.concurrent.TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork("sincronizar_compras_$uid", ExistingWorkPolicy.APPEND_OR_REPLACE, syncRequest)
    }

    private suspend fun subirAFirebaseYRestarStock(uid: String, compraId: String, eventoId: String, nombreEvento: String, fecha: String, hora: String, precio: Double, direccion: String, fotoPath: String, timestamp: Long) {
        val data = mapOf("eventoId" to eventoId, "nombreEvento" to nombreEvento, "fecha" to fecha, "hora" to hora, "precio" to precio, "direccionEntrega" to direccion, "fotoInePath" to fotoPath, "timestamp" to timestamp)
        firebaseDatabase.getReference("compras").child(uid).child(compraId).setValue(data).await()
        restarStockUseCase(eventoId)
        compraDao.marcarSincronizada(compraId)
    }

    private fun crearCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Proceso de Compra", NotificationManager.IMPORTANCE_LOW))
        }
    }

    private fun buildNotificacion(mensaje: String) = NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("SweepTickets").setContentText(mensaje).setOngoing(true).build()

    private fun mostrarNotificacionFinal(id: Int, titulo: String, cuerpo: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(id, NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(titulo).setContentText(cuerpo).setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true).build())
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

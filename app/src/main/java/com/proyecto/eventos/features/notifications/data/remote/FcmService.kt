//com.proyecto.eventos.features.notifications.data.remote.FcmService.kt
package com.proyecto.eventos.features.notifications.data.remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.proyecto.eventos.MainActivity
import com.proyecto.eventos.features.auth.data.local.FcmTokenDao
import com.proyecto.eventos.features.auth.data.local.FcmTokenEntity
import com.proyecto.eventos.features.notifications.data.local.NotificationDao
import com.proyecto.eventos.features.notifications.data.local.NotificationEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenDao: FcmTokenDao

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firebaseDatabase: FirebaseDatabase

    @Inject
    lateinit var notificationDao: NotificationDao

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = firebaseAuth.currentUser?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            fcmTokenDao.guardarToken(
                FcmTokenEntity(uid = uid, token = token)
            )
            firebaseDatabase
                .getReference("fcm_tokens")
                .child(uid)
                .setValue(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val uid = firebaseAuth.currentUser?.uid ?: "global"

        val titulo = message.notification?.title
            ?: message.data["titulo"]
            ?: "SweepTickets"

        val cuerpo = message.notification?.body
            ?: message.data["cuerpo"]
            ?: "Tienes una nueva notificación"

        // Guardar en Room para el historial de notificaciones con el userId
        CoroutineScope(Dispatchers.IO).launch {
            notificationDao.insertar(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    userId = uid,
                    titulo = titulo,
                    cuerpo = cuerpo
                )
            )
        }

        mostrarNotificacion(titulo, cuerpo)
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "sweeptickets_fcm"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "SweepTickets Notificaciones",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notificaciones de eventos y compras"
                }
            )
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        nm.notify(System.currentTimeMillis().toInt(), notificacion)
    }
}
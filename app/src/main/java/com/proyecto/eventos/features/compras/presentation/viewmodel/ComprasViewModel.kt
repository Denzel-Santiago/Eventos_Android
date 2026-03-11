package com.proyecto.eventos.features.compras.presentation.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.eventos.features.compras.domain.entities.CompraEntidad
import com.proyecto.eventos.features.compras.domain.usecases.GuardarCompraUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ComprasViewModel @Inject constructor(
    private val guardarCompraUseCase: GuardarCompraUseCase,
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComprasUiState())
    val uiState: StateFlow<ComprasUiState> = _uiState.asStateFlow()

    fun onNombreChange(nombre: String) {
        _uiState.value = _uiState.value.copy(nombreIngresado = nombre)
    }

    fun setFotoPath(path: String) {
        _uiState.value = _uiState.value.copy(fotoInePath = path, fotoTomada = true)
    }

    fun setDireccion(direccion: String) {
        _uiState.value = _uiState.value.copy(direccionEntrega = direccion, gpsObtenido = true)
    }

    fun validarNombre() {
        val nombreCuenta = firebaseAuth.currentUser?.displayName
            ?: firebaseAuth.currentUser?.email?.substringBefore("@")
            ?: ""
        val nombreIngresado = _uiState.value.nombreIngresado.trim()

        // Validación flexible: contiene parte del nombre
        val valido = nombreIngresado.isNotBlank() && (
            nombreCuenta.contains(nombreIngresado, ignoreCase = true) ||
            nombreIngresado.contains(nombreCuenta, ignoreCase = true) ||
            nombreIngresado.length >= 3
        )

        _uiState.value = _uiState.value.copy(nombreValidado = valido)
    }

    fun terminarCompra(
        eventoId: String,
        nombreEvento: String,
        fecha: String,
        hora: String,
        precio: Double
    ) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)

            val compra = CompraEntidad(
                eventoId = eventoId,
                nombreEvento = nombreEvento,
                fecha = fecha,
                hora = hora,
                precio = precio,
                direccionEntrega = state.direccionEntrega,
                fotoInePath = state.fotoInePath,
                timestamp = System.currentTimeMillis()
            )

            val result = guardarCompraUseCase(uid, compra)

            result.fold(
                onSuccess = {
                    enviarNotificacion(nombreEvento)
                    vibrar()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        compraExitosa = true
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al guardar la compra: ${it.message}"
                    )
                }
            )
        }
    }

    private fun enviarNotificacion(nombreEvento: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val channelId = "compras_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Compras",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de compras de boletos"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡Compra exitosa! 🎫")
            .setContentText("Tu boleto para $nombreEvento ha sido confirmado")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun vibrar() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                        as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 300, 100, 300),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 300, 100, 300),
                            -1
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 300, 100, 300), -1)
                }
            }
        } catch (e: Exception) {
            // Si falla la vibración, no interrumpir el flujo
        }
    }

    data class ComprasUiState(
        val isLoading: Boolean = false,
        val fotoTomada: Boolean = false,
        val fotoInePath: String = "",
        val gpsObtenido: Boolean = false,
        val direccionEntrega: String = "",
        val nombreIngresado: String = "",
        val nombreValidado: Boolean = false,
        val compraExitosa: Boolean = false,
        val error: String? = null
    )
}

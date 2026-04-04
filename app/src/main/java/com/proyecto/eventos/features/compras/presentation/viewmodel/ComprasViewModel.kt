package com.proyecto.eventos.features.compras.presentation.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.proyecto.eventos.core.hardware.camera.CameraManager
import com.proyecto.eventos.core.hardware.location.LocationManager
import com.proyecto.eventos.core.hardware.vibration.VibrationManager
import com.proyecto.eventos.features.compras.domain.entities.CompraEntidad
import com.proyecto.eventos.features.compras.domain.usecases.GuardarCompraUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ComprasViewModel @Inject constructor(
    private val guardarCompraUseCase: GuardarCompraUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val cameraManager: CameraManager,
    private val locationManager: LocationManager,
    private val vibrationManager: VibrationManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComprasUiState())
    val uiState: StateFlow<ComprasUiState> = _uiState.asStateFlow()

    private var nombreRealUsuario: String = ""

    init {
        cargarNombreUsuario()
    }

    private fun cargarNombreUsuario() {
        viewModelScope.launch {
            val uid = firebaseAuth.currentUser?.uid ?: return@launch
            try {
                val snapshot = firebaseDatabase
                    .getReference("usuarios")
                    .child(uid)
                    .get()
                    .await()
                nombreRealUsuario = snapshot.child("nombre")
                    .getValue(String::class.java) ?: ""
            } catch (e: Exception) {
                nombreRealUsuario = ""
            }
        }
    }

    fun setDireccion(direccion: String) {
        _uiState.value = _uiState.value.copy(direccionEntrega = direccion, gpsObtenido = true)
    }

    /**
     * Inicializa la cámara
     */
    fun initializeCamera() {
        cameraManager.initialize()
    }

    /**
     * Toma una foto usando el CameraManager
     */
    fun takePhoto(onResult: (String) -> Unit) {
        cameraManager.takePicture(onResult)
    }

    /**
     * Libera los recursos de la cámara
     */
    fun releaseCamera() {
        cameraManager.release()
    }

    /**
     * Obtiene la ubicación actual usando el LocationManager
     */
    suspend fun obtenerUbicacionActual(): Result<String> {
        return locationManager.getCurrentLocation()
    }

    /**
     * Lee el texto de la foto con ML Kit OCR y lo muestra al usuario para que confirme.
     */
    fun analizarFotoINE(fotoPath: String) {
        _uiState.value = _uiState.value.copy(
            fotoInePath = fotoPath,
            analizandoINE = true,
            textoDetectado = "",
            errorINE = null,
            ineConfirmada = false
        )

        viewModelScope.launch {
            try {
                val file = File(fotoPath)
                if (!file.exists()) {
                    _uiState.value = _uiState.value.copy(
                        analizandoINE = false,
                        errorINE = "No se pudo leer la foto. Intenta de nuevo."
                    )
                    return@launch
                }

                val image = InputImage.fromFilePath(
                    context,
                    android.net.Uri.fromFile(file)
                )
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                val result = recognizer.process(image).await()

                val texto = result.text.trim()

                if (texto.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        analizandoINE = false,
                        errorINE = "No se detectó texto en la imagen. Asegúrate de tomar la foto con buena iluminación."
                    )
                    return@launch
                }

                // Verificar si el nombre del usuario aparece en el texto detectado
                val nombreEncontrado = if (nombreRealUsuario.isNotBlank()) {
                    val textoUpper = texto.uppercase()
                    val nombreUpper = nombreRealUsuario.uppercase()
                    // Buscar al menos una palabra del nombre (de más de 3 letras)
                    val palabras = nombreUpper.split(" ").filter { it.length > 3 }
                    palabras.any { palabra -> textoUpper.contains(palabra) }
                } else false

                _uiState.value = _uiState.value.copy(
                    analizandoINE = false,
                    textoDetectado = texto,
                    nombreEncontradoEnINE = nombreEncontrado,
                    mostrandoConfirmacion = true  // mostrar diálogo de confirmación
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    analizandoINE = false,
                    errorINE = "Error al analizar la imagen: ${e.message}"
                )
            }
        }
    }

    /** El usuario confirma que el texto mostrado corresponde a su INE */
    fun confirmarINE() {
        val state = _uiState.value
        if (!state.nombreEncontradoEnINE && nombreRealUsuario.isNotBlank()) {
            // Nombre no encontrado — rechazar
            _uiState.value = state.copy(
                mostrandoConfirmacion = false,
                fotoTomada = false,
                fotoInePath = "",
                textoDetectado = "",
                errorINE = "Tu nombre \"$nombreRealUsuario\" no aparece en la INE. Usa tu propia credencial."
            )
        } else {
            // OK — marcar foto e identidad como verificados
            vibrationManager.vibrateLight()
            _uiState.value = state.copy(
                mostrandoConfirmacion = false,
                fotoTomada = true,
                ineConfirmada = true,
                nombreValidado = true,
                errorINE = null
            )
        }
    }

    /** El usuario rechaza — quiere retomar la foto */
    fun rechazarINE() {
        _uiState.value = _uiState.value.copy(
            mostrandoConfirmacion = false,
            fotoTomada = false,
            fotoInePath = "",
            textoDetectado = "",
            ineConfirmada = false,
            errorINE = null
        )
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

            guardarCompraUseCase(uid, compra).fold(
                onSuccess = {
                    enviarNotificacion(nombreEvento)
                    vibrationManager.vibrateSuccess()
                    _uiState.value = _uiState.value.copy(isLoading = false, compraExitosa = true)
                },
                onFailure = {
                    vibrationManager.vibrateError()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al guardar la compra: ${it.message}"
                    )
                }
            )
        }
    }

    private fun enviarNotificacion(nombreEvento: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "compras_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(channelId, "Compras", NotificationManager.IMPORTANCE_HIGH)
                    .apply { description = "Notificaciones de compras" }
            )
        }
        nm.notify(
            System.currentTimeMillis().toInt(),
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("¡Compra exitosa! 🎫")
                .setContentText("Tu boleto para $nombreEvento ha sido confirmado")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
        )
    }

    data class ComprasUiState(
        val isLoading: Boolean = false,
        // INE
        val analizandoINE: Boolean = false,
        val fotoTomada: Boolean = false,
        val fotoInePath: String = "",
        val textoDetectado: String = "",
        val nombreEncontradoEnINE: Boolean = false,
        val mostrandoConfirmacion: Boolean = false,
        val ineConfirmada: Boolean = false,
        val errorINE: String? = null,
        // GPS
        val gpsObtenido: Boolean = false,
        val direccionEntrega: String = "",
        // Identidad
        val nombreIngresado: String = "",
        val nombreValidado: Boolean = false,
        // Flujo
        val compraExitosa: Boolean = false,
        val error: String? = null
    )
}

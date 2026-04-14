//com.proyecto.eventos.features.compras.presentation.viewmodel.ComprasViewModel.kt
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
import com.proyecto.eventos.core.network.NetworkMonitor
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
    private val networkMonitor: NetworkMonitor,
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

            // Sin internet: no intentar cargar desde Firebase
            if (!networkMonitor.isConnected()) return@launch

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

    fun initializeCamera() {
        cameraManager.initialize()
    }

    fun takePhoto(onResult: (String) -> Unit) {
        cameraManager.takePicture(onResult)
    }

    fun releaseCamera() {
        cameraManager.release()
    }

    suspend fun obtenerUbicacionActual(): Result<String> {
        return locationManager.getCurrentLocation()
    }

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

                val nombreEncontrado = if (nombreRealUsuario.isNotBlank()) {
                    val textoUpper = texto.uppercase()
                    val nombreUpper = nombreRealUsuario.uppercase()
                    val palabras = nombreUpper.split(" ").filter { it.length > 3 }
                    palabras.any { palabra -> textoUpper.contains(palabra) }
                } else false

                _uiState.value = _uiState.value.copy(
                    analizandoINE = false,
                    textoDetectado = texto,
                    nombreEncontradoEnINE = nombreEncontrado,
                    mostrandoConfirmacion = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    analizandoINE = false,
                    errorINE = "Error al analizar la imagen: ${e.message}"
                )
            }
        }
    }

    fun confirmarINE() {
        val state = _uiState.value
        if (!state.nombreEncontradoEnINE && nombreRealUsuario.isNotBlank()) {
            _uiState.value = state.copy(
                mostrandoConfirmacion = false,
                fotoTomada = false,
                fotoInePath = "",
                textoDetectado = "",
                errorINE = "Tu nombre \"$nombreRealUsuario\" no aparece en la INE. Usa tu propia credencial."
            )
        } else {
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
        // ELIMINADO: Bloqueo de red. Ahora permitimos compras offline.
        
        val uid = firebaseAuth.currentUser?.uid ?: return
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            // Si hay internet, intentamos verificar stock en tiempo real
            if (networkMonitor.isConnected()) {
                val tieneStock = try {
                    val snapshot = firebaseDatabase
                        .getReference("eventos")
                        .child(eventoId)
                        .child("stock")
                        .get()
                        .await()
                    (snapshot.getValue(Int::class.java) ?: 0) > 0
                } catch (e: Exception) {
                    true // En caso de error, dejamos que el servicio lo intente
                }

                if (!tieneStock) {
                    vibrationManager.vibrateError()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Stock agotado"
                    )
                    return@launch
                }
            }

            // Iniciar el ForegroundService para procesar la compra (Offline o Online)
            val compraId = java.util.UUID.randomUUID().toString()
            val intent = com.proyecto.eventos.features.compras.data.service.CompraForegroundService
                .buildIntent(
                    context = context,
                    uid = uid,
                    compraId = compraId,
                    eventoId = eventoId,
                    nombreEvento = nombreEvento,
                    fecha = fecha,
                    hora = hora,
                    precio = precio,
                    direccion = state.direccionEntrega,
                    fotoPath = state.fotoInePath,
                    timestamp = System.currentTimeMillis()
                )

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }

            vibrationManager.vibrateSuccess()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                compraExitosa = true
            )
        }
    }

    data class ComprasUiState(
        val isLoading: Boolean = false,
        val analizandoINE: Boolean = false,
        val fotoTomada: Boolean = false,
        val fotoInePath: String = "",
        val textoDetectado: String = "",
        val nombreEncontradoEnINE: Boolean = false,
        val mostrandoConfirmacion: Boolean = false,
        val ineConfirmada: Boolean = false,
        val errorINE: String? = null,
        val gpsObtenido: Boolean = false,
        val direccionEntrega: String = "",
        val nombreIngresado: String = "",
        val nombreValidado: Boolean = false,
        val compraExitosa: Boolean = false,
        val error: String? = null
    )
}

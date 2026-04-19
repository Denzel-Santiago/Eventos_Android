//com.proyecto.eventos.features.eventos.presentation.viewmodel.AdminEventosViewModel
package com.proyecto.eventos.features.eventos.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.eventos.domain.usecases.CreateEventoUseCase
import com.proyecto.eventos.features.eventos.domain.usecases.DeleteEventoUseCase
import com.proyecto.eventos.features.eventos.domain.usecases.GetEventosUseCase
import com.proyecto.eventos.features.eventos.domain.usecases.UpdateEventoUseCase
import com.proyecto.eventos.features.notifications.data.remote.FcmApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AdminEventosViewModel @Inject constructor(
    private val getEventosUseCase: GetEventosUseCase,
    private val createEventoUseCase: CreateEventoUseCase,
    private val updateEventoUseCase: UpdateEventoUseCase,
    private val deleteEventoUseCase: DeleteEventoUseCase,
    private val firebaseDatabase: FirebaseDatabase,
    private val fcmApiService: FcmApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminEventosUiState())
    val uiState: StateFlow<AdminEventosUiState> = _uiState.asStateFlow()

    private val _eventos = MutableStateFlow<List<EventoEntidad>>(emptyList())
    val eventos: StateFlow<List<EventoEntidad>> = _eventos.asStateFlow()

    init {
        cargarEventos()
    }

    private fun cargarEventos() {
        viewModelScope.launch {
            getEventosUseCase()
                .catch { _uiState.value = _uiState.value.copy(error = it.message) }
                .collect { _eventos.value = it }
        }
    }

    fun abrirDialogoNuevo() {
        _uiState.value = AdminEventosUiState(mostrarDialog = true)
    }

    fun abrirDialogoEditar(evento: EventoEntidad) {
        _uiState.value = AdminEventosUiState(
            mostrarDialog = true,
            eventoEditando = evento,
            dialogNombre = evento.nombre,
            dialogFecha = evento.fecha,
            dialogHora = evento.hora,
            dialogUbicacion = evento.ubicacion,
            dialogPrecio = evento.precio.toString(),
            dialogStock = evento.stock.toString(),
            // Si ya tiene imagen guardada en Firebase, la mostramos
            imagenBase64Actual = evento.imagen
        )
    }

    fun cerrarDialogo() {
        _uiState.value = AdminEventosUiState()
    }

    // Campos del diálogo
    fun onNombreChange(v: String) { _uiState.value = _uiState.value.copy(dialogNombre = v) }
    fun onFechaChange(v: String) { _uiState.value = _uiState.value.copy(dialogFecha = v) }
    fun onHoraChange(v: String) { _uiState.value = _uiState.value.copy(dialogHora = v) }
    fun onUbicacionChange(v: String) { _uiState.value = _uiState.value.copy(dialogUbicacion = v) }
    fun onPrecioChange(v: String) { _uiState.value = _uiState.value.copy(dialogPrecio = v) }
    fun onStockChange(v: String) { _uiState.value = _uiState.value.copy(dialogStock = v) }

    /**
     * Recibe la URI de la imagen seleccionada por el usuario,
     * la convierte a Base64 y la guarda en el estado.
     */
    fun onImagenSeleccionada(uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(subiendoImagen = true, error = null)

                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: return@launch
                inputStream.close()

                // Comprimir si es muy grande (límite ~800KB para Firebase)
                val base64 = if (bytes.size > 800_000) {
                    // Comprimir con BitmapFactory
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    val outputStream = java.io.ByteArrayOutputStream()
                    // Reducir calidad hasta que quepa
                    var calidad = 80
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, calidad, outputStream)
                    while (outputStream.size() > 800_000 && calidad > 20) {
                        outputStream.reset()
                        calidad -= 10
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, calidad, outputStream)
                    }
                    Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
                } else {
                    Base64.encodeToString(bytes, Base64.DEFAULT)
                }

                _uiState.value = _uiState.value.copy(
                    imagenBase64Nueva = base64,
                    imagenUriPreview = uri,
                    subiendoImagen = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    subiendoImagen = false,
                    error = "Error al procesar la imagen: ${e.message}"
                )
            }
        }
    }

    fun guardarEvento() {
        val state = _uiState.value

        if (state.dialogNombre.isBlank() || state.dialogFecha.isBlank() ||
            state.dialogHora.isBlank() || state.dialogUbicacion.isBlank()) {
            _uiState.value = state.copy(error = "Todos los campos son requeridos")
            return
        }

        val precio = state.dialogPrecio.toDoubleOrNull() ?: run {
            _uiState.value = state.copy(error = "Precio inválido")
            return
        }
        val stock = state.dialogStock.toIntOrNull() ?: run {
            _uiState.value = state.copy(error = "Stock inválido")
            return
        }

        // Imagen: usar la nueva si se seleccionó, si no usar la que ya tenía
        val imagenFinal = when {
            state.imagenBase64Nueva.isNotBlank() -> state.imagenBase64Nueva
            state.imagenBase64Actual.isNotBlank() -> state.imagenBase64Actual
            else -> ""
        }

        val evento = EventoEntidad(
            id = state.eventoEditando?.id ?: "",
            nombre = state.dialogNombre,
            fecha = state.dialogFecha,
            hora = state.dialogHora,
            ubicacion = state.dialogUbicacion,
            precio = precio,
            stock = stock,
            imagen = imagenFinal
        )

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)
            try {
                if (state.eventoEditando == null) {
                    createEventoUseCase(evento)
                    // Notificar a todos los usuarios del nuevo evento
                    notificarNuevoEvento(evento.nombre, evento.ubicacion)
                } else {
                    updateEventoUseCase(evento)
                }
                _uiState.value = AdminEventosUiState() // resetear
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al guardar: ${e.message}"
                )
            }
        }
    }

    private suspend fun notificarNuevoEvento(nombreEvento: String, ubicacion: String) {
        try {
            // Leer todos los tokens de fcm_tokens/ en Firebase
            val snapshot = firebaseDatabase
                .getReference("fcm_tokens")
                .get()
                .await()

            // Iterar cada uid y su token
            snapshot.children.forEach { child ->
                val token = child.getValue(String::class.java) ?: return@forEach
                // Enviar notificación a cada dispositivo
                fcmApiService.enviarNotificacion(
                    token = token,
                    titulo = "¡Nuevo evento disponible! 🎉",
                    cuerpo = "$nombreEvento en $ubicacion. ¡No te lo pierdas!",
                    eventoId = "",
                    nombreEvento = nombreEvento
                )
            }
        } catch (e: Exception) {
            // No interrumpir el flujo si falla la notificación
            android.util.Log.e("AdminEventosVM", "Error enviando notificaciones: ${e.message}")
        }
    }

    fun eliminarEvento(eventoId: String) {
        viewModelScope.launch {
            deleteEventoUseCase(eventoId)
        }
    }

    data class AdminEventosUiState(
        val isLoading: Boolean = false,
        val mostrarDialog: Boolean = false,
        val eventoEditando: EventoEntidad? = null,
        // Campos del formulario
        val dialogNombre: String = "",
        val dialogFecha: String = "",
        val dialogHora: String = "",
        val dialogUbicacion: String = "",
        val dialogPrecio: String = "",
        val dialogStock: String = "",
        // Imagen
        val imagenUriPreview: Uri? = null,       // URI local para preview
        val imagenBase64Nueva: String = "",      // Base64 recién seleccionada
        val imagenBase64Actual: String = "",     // Base64 ya guardada en Firebase
        val subiendoImagen: Boolean = false,
        val error: String? = null
    )
}

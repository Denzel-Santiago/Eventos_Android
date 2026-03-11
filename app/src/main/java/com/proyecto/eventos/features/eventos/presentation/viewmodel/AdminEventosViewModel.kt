//features/Eventos/presentation/viewmodel/AdminEventosViewModel.kt
package com.proyecto.eventos.features.eventos.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.eventos.domain.usecases.CreateEventoUseCase
import com.proyecto.eventos.features.eventos.domain.usecases.DeleteEventoUseCase
import com.proyecto.eventos.features.eventos.domain.usecases.GetEventosUseCase
import com.proyecto.eventos.features.eventos.domain.usecases.UpdateEventoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminEventosViewModel @Inject constructor(
    private val getEventosUseCase: GetEventosUseCase,
    private val createEventoUseCase: CreateEventoUseCase,
    private val updateEventoUseCase: UpdateEventoUseCase,
    private val deleteEventoUseCase: DeleteEventoUseCase
) : ViewModel() {

    private val _eventos = MutableStateFlow<List<EventoEntidad>>(emptyList())
    val eventos = _eventos.asStateFlow()

    var mostrarFormulario by mutableStateOf(false)
        private set

    var eventoActual by mutableStateOf<EventoEntidad?>(null)
        private set

    var nombre by mutableStateOf("")
    var ubicacion by mutableStateOf("")
    var fecha by mutableStateOf("")
    var hora by mutableStateOf("")
    var stock by mutableStateOf("")
    var precio by mutableStateOf("")
    var imagen by mutableStateOf("")

    init {
        cargarEventos()
    }

    fun cargarEventos() {
        viewModelScope.launch {
            getEventosUseCase()
                .catch { _eventos.value = emptyList() }
                .collect { _eventos.value = it }
        }
    }

    fun abrirNuevoEvento() {
        eventoActual = null
        limpiarCampos()
        mostrarFormulario = true
    }

    fun editarEvento(evento: EventoEntidad) {
        eventoActual = evento
        nombre = evento.nombre
        ubicacion = evento.ubicacion
        fecha = evento.fecha
        hora = evento.hora
        stock = evento.stock.toString()
        precio = evento.precio.toString()
        imagen = evento.imagen
        mostrarFormulario = true
    }

    fun guardarEvento() {
        viewModelScope.launch {
            val evento = EventoEntidad(
                id = eventoActual?.id ?: "",
                nombre = nombre,
                ubicacion = ubicacion,
                fecha = fecha,
                hora = hora,
                stock = stock.toIntOrNull() ?: 0,
                precio = precio.toDoubleOrNull() ?: 0.0,
                imagen = imagen
            )
            if (eventoActual == null) {
                createEventoUseCase(evento)
            } else {
                updateEventoUseCase(evento)
            }
            cerrarFormulario()
        }
    }

    fun eliminarEvento(id: String) {
        viewModelScope.launch {
            deleteEventoUseCase(id)
        }
    }

    fun cerrarFormulario() {
        mostrarFormulario = false
        eventoActual = null
        limpiarCampos()
    }

    private fun limpiarCampos() {
        nombre = ""; ubicacion = ""; fecha = ""; hora = ""
        stock = ""; precio = ""; imagen = ""
    }

    fun onNombreChange(v: String) { nombre = v }
    fun onUbicacionChange(v: String) { ubicacion = v }
    fun onFechaChange(v: String) { fecha = v }
    fun onHoraChange(v: String) { hora = v }
    fun onStockChange(v: String) { stock = v }
    fun onPrecioChange(v: String) { precio = v }
    fun onImagenChange(v: String) { imagen = v }
}
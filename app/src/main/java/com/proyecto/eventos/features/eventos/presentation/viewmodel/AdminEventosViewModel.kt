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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminEventosViewModel(
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
    var boletos by mutableStateOf("")
    var precio by mutableStateOf("")

    init {
        cargarEventos()
    }

    fun cargarEventos() {
        viewModelScope.launch {
            try {
                val listaEventos = getEventosUseCase.execute()
                _eventos.value = listaEventos
            } catch (e: Exception) {
                _eventos.value = emptyList()
            }
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
        boletos = evento.boletosDisponibles.toString()
        precio = evento.precio.toString()
        mostrarFormulario = true
    }

    fun guardarEvento() {
        viewModelScope.launch {
            val boletosInt = boletos.toIntOrNull() ?: 0
            val precioDouble = precio.toDoubleOrNull() ?: 0.0

            val evento = EventoEntidad(
                id = eventoActual?.id ?: 0,
                nombre = nombre,
                ubicacion = ubicacion,
                fecha = fecha,
                boletosDisponibles = boletosInt,
                precio = precioDouble
            )

            if (eventoActual == null) {
                createEventoUseCase.execute(evento)
            } else {
                updateEventoUseCase.execute(evento)
            }

            cargarEventos()
            cerrarFormulario()
        }
    }

    fun eliminarEvento(id: Int) {
        viewModelScope.launch {
            deleteEventoUseCase.execute(id)
            cargarEventos()
        }
    }

    fun cerrarFormulario() {
        mostrarFormulario = false
        eventoActual = null
        limpiarCampos()
    }

    private fun limpiarCampos() {
        nombre = ""
        ubicacion = ""
        fecha = ""
        boletos = ""
        precio = ""
    }

    fun onNombreChange(value: String) {
        nombre = value
    }

    fun onUbicacionChange(value: String) {
        ubicacion = value
    }

    fun onFechaChange(value: String) {
        fecha = value
    }

    fun onBoletosChange(value: String) {
        boletos = value
    }

    fun onPrecioChange(value: String) {
        precio = value
    }
}
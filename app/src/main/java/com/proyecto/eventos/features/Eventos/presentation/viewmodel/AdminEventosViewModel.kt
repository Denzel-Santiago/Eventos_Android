package com.proyecto.eventos.features.Eventos.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.proyecto.eventos.features.Eventos.domain.entities.EventoAdminEntidad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminEventosViewModel : ViewModel() {

    private val _eventos = MutableStateFlow<List<EventoAdminEntidad>>(emptyList())
    val eventos = _eventos.asStateFlow()

    var mostrarFormulario by mutableStateOf(false)
        private set

    var eventoActual by mutableStateOf<EventoAdminEntidad?>(null)

    init {
        cargarEventos()
    }

    private fun cargarEventos() {
        _eventos.value = listOf(
            EventoAdminEntidad(1, "Tomorrowland", "Bélgica", "Julio 2025", 350.0, 120),
            EventoAdminEntidad(2, "EDC", "Las Vegas", "Mayo 2025", 280.0, 80)
        )
    }

    fun abrirNuevoEvento() {
        eventoActual = EventoAdminEntidad(0, "", "", "", 0.0, 0)
        mostrarFormulario = true
    }

    fun editarEvento(evento: EventoAdminEntidad) {
        eventoActual = evento.copy()
        mostrarFormulario = true
    }

    fun guardarEvento() {
        val lista = _eventos.value.toMutableList()

        if (eventoActual?.id == 0) {
            val nuevoId = (lista.maxOfOrNull { it.id } ?: 0) + 1
            lista.add(eventoActual!!.copy(id = nuevoId))
        } else {
            val index = lista.indexOfFirst { it.id == eventoActual!!.id }
            lista[index] = eventoActual!!
        }

        _eventos.value = lista
        cerrarFormulario()
    }

    fun eliminarEvento(id: Int) {
        _eventos.value = _eventos.value.filterNot { it.id == id }
    }

    fun cerrarFormulario() {
        mostrarFormulario = false
        eventoActual = null
    }
}

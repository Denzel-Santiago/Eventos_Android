package com.proyecto.eventos.features.Eventos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.proyecto.eventos.features.Eventos.domain.entities.EventoCompraEntidad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CompraViewModel : ViewModel() {

    private val _eventos = MutableStateFlow<List<EventoCompraEntidad>>(emptyList())
    val eventos = _eventos.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    fun cargarEventos() {
        // 🔹 Datos simulados (luego API real)
        _eventos.value = listOf(
            EventoCompraEntidad(
                nombre = "Tomorrowland",
                ubicacion = "Boom, Bélgica",
                fecha = "Julio 2025",
                boletosDisponibles = 120,
                precio = 350.0
            ),
            EventoCompraEntidad(
                nombre = "EDC",
                ubicacion = "Las Vegas",
                fecha = "Mayo 2025",
                boletosDisponibles = 80,
                precio = 280.0
            )
        )
    }

    fun comprarBoletos(evento: EventoCompraEntidad) {
        // 🔹 Compra simulada
        _mensaje.value = "Compra realizada para ${evento.nombre}"
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}

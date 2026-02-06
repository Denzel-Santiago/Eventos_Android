package com.proyecto.eventos.features.eventos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.eventos.domain.usecases.GetEventosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompraViewModel(
    private val getEventosUseCase: GetEventosUseCase
) : ViewModel() {

    private val _eventos = MutableStateFlow<List<EventoEntidad>>(emptyList())
    val eventos = _eventos.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

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

    fun comprarBoleto(evento: EventoEntidad) {
        _mensaje.value = "Compra realizada para ${evento.nombre}"
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
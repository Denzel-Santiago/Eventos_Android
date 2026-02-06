package com.proyecto.eventos.features.eventos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.eventos.domain.usecases.GetEventosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InicioViewModel(
    private val getEventosUseCase: GetEventosUseCase
) : ViewModel() {

    private val _eventos = MutableStateFlow<List<EventoEntidad>>(emptyList())
    val eventos = _eventos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        cargarEventos()
    }

    fun cargarEventos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val listaEventos = getEventosUseCase.execute()
                _eventos.value = listaEventos
            } catch (e: Exception) {
                _eventos.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
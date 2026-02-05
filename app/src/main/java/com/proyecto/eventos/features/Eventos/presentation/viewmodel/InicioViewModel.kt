package com.proyecto.eventos.features.Eventos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.proyecto.eventos.R
import com.proyecto.eventos.features.Eventos.domain.entities.EventoEntidad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InicioViewModel : ViewModel() {

    private val _eventos = MutableStateFlow<List<EventoEntidad>>(emptyList())
    val eventos = _eventos.asStateFlow()

    fun cargarEventos() {
        _eventos.value = listOf(
            EventoEntidad(
                nombre = "Tomorrowland",
                descripcion = "Boom, Bélgica · Julio · EDM",
                imagen = R.drawable.tomoroowland
            ),
            EventoEntidad(
                nombre = "EDC",
                descripcion = "Las Vegas · Mayo · EDM",
                imagen = R.drawable.edc
            ),
            EventoEntidad(
                nombre = "Ultra Music Festival",
                descripcion = "Miami · Marzo · EDM",
                imagen = R.drawable.ultra
            )
        )
    }
}

package com.proyecto.eventos.features.favoritos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.favoritos.domain.usecases.AgregarFavoritoUseCase
import com.proyecto.eventos.features.favoritos.domain.usecases.EliminarFavoritoUseCase
import com.proyecto.eventos.features.favoritos.domain.usecases.GetFavoritosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    private val getFavoritosUseCase: GetFavoritosUseCase,
    private val agregarFavoritoUseCase: AgregarFavoritoUseCase,
    private val eliminarFavoritoUseCase: EliminarFavoritoUseCase
) : ViewModel() {

    private val _favoritos = MutableStateFlow<List<EventoEntidad>>(emptyList())
    val favoritos = _favoritos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        viewModelScope.launch {
            _isLoading.value = true
            getFavoritosUseCase()
                .catch { _favoritos.value = emptyList() }
                .collect {
                    _favoritos.value = it
                    _isLoading.value = false
                }
        }
    }

    fun agregarFavorito(evento: EventoEntidad) {
        viewModelScope.launch {
            agregarFavoritoUseCase(evento)
        }
    }

    fun eliminarFavorito(eventoId: String) {
        viewModelScope.launch {
            eliminarFavoritoUseCase(eventoId)
        }
    }
}

package com.proyecto.eventos.features.historial.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.eventos.features.compras.domain.entities.CompraEntidad
import com.proyecto.eventos.features.compras.domain.usecases.GetHistorialUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val getHistorialUseCase: GetHistorialUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val uid get() = firebaseAuth.currentUser?.uid ?: ""

    private val _historial = MutableStateFlow<List<CompraEntidad>>(emptyList())
    val historial = _historial.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        cargarHistorial()
    }

    private fun cargarHistorial() {
        viewModelScope.launch {
            _isLoading.value = true
            getHistorialUseCase(uid)
                .catch { _historial.value = emptyList() }
                .collect {
                    _historial.value = it
                    _isLoading.value = false
                }
        }
    }
}

package com.proyecto.eventos.features.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.features.login.data.repositories.AuthRepository
import com.proyecto.eventos.features.login.domain.entities.UsuarioEntidad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminUsuariosViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _usuarios = MutableStateFlow<List<UsuarioEntidad>>(emptyList())
    val usuarios = _usuarios.asStateFlow()

    fun eliminarUsuario(id: Int) {
        viewModelScope.launch {
            repository.eliminarUsuario(id)
        }
    }
}
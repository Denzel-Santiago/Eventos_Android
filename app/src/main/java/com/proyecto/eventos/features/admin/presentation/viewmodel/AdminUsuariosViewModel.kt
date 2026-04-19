//com.proyecto.eventos.features.admin.presentation.viewmodel.AdminUsuariosViewModel
package com.proyecto.eventos.features.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.features.admin.domain.entities.UsuarioAdminEntidad
import com.proyecto.eventos.features.admin.domain.usecases.EliminarUsuarioUseCase
import com.proyecto.eventos.features.admin.domain.usecases.GetUsuariosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminUsuariosViewModel @Inject constructor(
    private val getUsuariosUseCase: GetUsuariosUseCase,
    private val eliminarUsuarioUseCase: EliminarUsuarioUseCase
) : ViewModel() {

    private val _usuarios = MutableStateFlow<List<UsuarioAdminEntidad>>(emptyList())
    val usuarios: StateFlow<List<UsuarioAdminEntidad>> = _usuarios.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    // Para el diálogo de confirmación de eliminación
    private val _usuarioAEliminar = MutableStateFlow<UsuarioAdminEntidad?>(null)
    val usuarioAEliminar: StateFlow<UsuarioAdminEntidad?> = _usuarioAEliminar.asStateFlow()

    init {
        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        viewModelScope.launch {
            _isLoading.value = true
            getUsuariosUseCase()
                .catch {
                    _mensajeError.value = "Error al cargar usuarios"
                    _isLoading.value = false
                }
                .collect {
                    _usuarios.value = it
                    _isLoading.value = false
                }
        }
    }

    fun pedirConfirmacionEliminar(usuario: UsuarioAdminEntidad) {
        _usuarioAEliminar.value = usuario
    }

    fun cancelarEliminar() {
        _usuarioAEliminar.value = null
    }

    fun confirmarEliminar() {
        val uid = _usuarioAEliminar.value?.uid ?: return
        _usuarioAEliminar.value = null
        viewModelScope.launch {
            eliminarUsuarioUseCase(uid).onFailure {
                _mensajeError.value = "Error al eliminar usuario"
            }
        }
    }

    fun limpiarError() {
        _mensajeError.value = null
    }
}

package com.proyecto.eventos.features.panel.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PanelViewModel @Inject constructor() : ViewModel() {

    // Estado del UI
    private val _uiState = MutableStateFlow(PanelUiState())
    val uiState: StateFlow<PanelUiState> = _uiState.asStateFlow()

    // Acciones
    private val _accion = MutableStateFlow<PanelAccion?>(null)
    val accion: StateFlow<PanelAccion?> = _accion.asStateFlow()

    init {
        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 🔥 SIMULACIÓN: Aquí irá la llamada real a Firebase Auth
            delay(1000)

            // Datos de prueba - luego vendrán de Firebase
            val esAdmin = false // Cambia a true para probar vista admin
            val nombreUsuario = if (esAdmin) "Admin Sistema" else "Juan Pérez"

            _uiState.update {
                it.copy(
                    isLoading = false,
                    userName = nombreUsuario,
                    userRole = if (esAdmin) "ADMIN" else "USER",
                    userEmail = "usuario@email.com"
                )
            }
        }
    }

    fun refreshUserData() {
        cargarDatosUsuario()
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 🔥 SIMULACIÓN: Aquí irá la llamada real a Firebase Auth
            delay(500)

            // Limpiar sesión
            _uiState.update {
                it.copy(
                    isLoading = false,
                    userName = "",
                    userRole = "",
                    userEmail = ""
                )
            }

            // Emitir acción de logout
            _accion.value = PanelAccion.Logout
        }
    }

    fun limpiarAccion() {
        _accion.value = null
    }

    // Estado del Panel
    data class PanelUiState(
        val isLoading: Boolean = false,
        val userName: String = "",
        val userEmail: String = "",
        val userRole: String = "USER", // "USER" o "ADMIN"
        val errorMessage: String? = null
    ) {
        val isAdmin: Boolean = userRole == "ADMIN"
        val welcomeMessage: String = if (userName.isNotEmpty()) "¡Hola, $userName!" else "¡Bienvenido!"
    }

    // Acciones que puede emitir el ViewModel
    sealed class PanelAccion {
        object Logout : PanelAccion()
        // Puedes agregar más acciones aquí si es necesario
    }
}

// Función de extensión para actualizar StateFlow
private fun <T> MutableStateFlow<T>.update(block: (T) -> T) {
    value = block(value)
}
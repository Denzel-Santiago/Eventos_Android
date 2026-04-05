//com.proyecto.eventos.features.panel.presentation.viewmodel.PanelViewModel.kt
package com.proyecto.eventos.features.panel.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.features.panel.domain.usecases.GetUsuarioActualUseCase
import com.proyecto.eventos.features.panel.domain.usecases.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PanelViewModel @Inject constructor(
    private val getUsuarioActualUseCase: GetUsuarioActualUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PanelUiState())
    val uiState: StateFlow<PanelUiState> = _uiState.asStateFlow()

    private val _accion = MutableStateFlow<PanelAccion?>(null)
    val accion: StateFlow<PanelAccion?> = _accion.asStateFlow()

    init {
        cargarDatosUsuario()
    }

    fun cargarDatosUsuario() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            android.util.Log.d("PanelDebug", "cargarDatosUsuario: iniciando")

            val usuario = getUsuarioActualUseCase()

            android.util.Log.d("PanelDebug", "cargarDatosUsuario: usuario = $usuario")

            if (usuario == null) {
                android.util.Log.d("PanelDebug", "cargarDatosUsuario: usuario null, NO haciendo logout")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "No se pudo cargar el perfil"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                userName  = usuario.nombre,
                userEmail = usuario.email,
                userRole  = usuario.rol,
                errorMessage = null
            )
        }
    }

    fun refreshUserData() {
        cargarDatosUsuario()
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            logoutUseCase()
            _uiState.value = PanelUiState()
            _accion.value = PanelAccion.Logout
        }
    }

    fun limpiarAccion() {
        _accion.value = null
    }

    data class PanelUiState(
        val isLoading: Boolean = false,
        val userName: String = "",
        val userEmail: String = "",
        val userRole: String = "usuario",
        val errorMessage: String? = null
    ) {
        val isAdmin: Boolean = userRole == "admin" || userRole == "ADMIN"
        val welcomeMessage: String =
            if (userName.isNotEmpty()) "¡Hola, $userName!" else "¡Bienvenido!"
    }

    sealed class PanelAccion {
        object Logout : PanelAccion()
    }
}
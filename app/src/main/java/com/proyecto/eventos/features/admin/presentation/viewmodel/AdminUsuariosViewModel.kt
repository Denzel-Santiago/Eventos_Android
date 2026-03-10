// features/login/presentation/viewmodel/AdminUsuariosViewModel.kt
package com.proyecto.eventos.features.admin.presentation.viewmodel
/*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.features.login.presentation.screens.UsuarioPreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminUsuariosViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUsuariosUiState())
    val uiState: StateFlow<AdminUsuariosUiState> = _uiState.asStateFlow()

    private val _usuarios = MutableStateFlow<List<UsuarioPreview>>(emptyList())
    val usuarios: StateFlow<List<UsuarioPreview>> = _usuarios.asStateFlow()

    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // SIMULACIÓN: Aquí irá la llamada a Firebase
            delay(1000)

            // Datos de prueba
            _usuarios.value = listOf(
                UsuarioPreview("1", "Juan Pérez", "juan@email.com", "USER"),
                UsuarioPreview("2", "María García", "maria@email.com", "USER"),
                UsuarioPreview("3", "Admin Sistema", "admin@email.com", "ADMIN"),
                UsuarioPreview("4", "Carlos López", "carlos@email.com", "USER")
            )

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun abrirDialogoNuevo() {
        _uiState.update {
            it.copy(
                mostrarDialog = true,
                usuarioEditando = null,
                dialogNombre = "",
                dialogEmail = "",
                dialogPassword = "",
                dialogRol = "USER"
            )
        }
    }

    fun abrirDialogoEditar(usuario: UsuarioPreview) {
        _uiState.update {
            it.copy(
                mostrarDialog = true,
                usuarioEditando = usuario,
                dialogNombre = usuario.nombre,
                dialogEmail = usuario.email,
                dialogPassword = "",
                dialogRol = usuario.rol
            )
        }
    }

    fun cerrarDialogo() {
        _uiState.update { it.copy(mostrarDialog = false) }
    }

    fun onDialogNombreChange(nombre: String) {
        _uiState.update { it.copy(dialogNombre = nombre) }
    }

    fun onDialogEmailChange(email: String) {
        _uiState.update { it.copy(dialogEmail = email) }
    }

    fun onDialogPasswordChange(password: String) {
        _uiState.update { it.copy(dialogPassword = password) }
    }

    fun onDialogRolChange(rol: String) {
        _uiState.update { it.copy(dialogRol = rol) }
    }

    fun guardarUsuario() {
        val state = _uiState.value

        // Validaciones
        if (state.dialogNombre.isBlank() || state.dialogEmail.isBlank()) {
            _uiState.update { it.copy(dialogError = "Nombre y email son requeridos") }
            return
        }

        if (state.usuarioEditando == null && state.dialogPassword.isBlank()) {
            _uiState.update { it.copy(dialogError = "Contraseña requerida para nuevo usuario") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // SIMULACIÓN: Aquí irá la llamada a Firebase
            delay(1000)

            if (state.usuarioEditando == null) {
                // Crear nuevo usuario
                val nuevoUsuario = UsuarioPreview(
                    id = System.currentTimeMillis().toString(),
                    nombre = state.dialogNombre,
                    email = state.dialogEmail,
                    rol = state.dialogRol
                )
                _usuarios.value = _usuarios.value + nuevoUsuario
            } else {
                // Editar usuario existente
                _usuarios.value = _usuarios.value.map { usuario ->
                    if (usuario.id == state.usuarioEditando?.id) {
                        usuario.copy(
                            nombre = state.dialogNombre,
                            email = state.dialogEmail,
                            rol = state.dialogRol
                        )
                    } else {
                        usuario
                    }
                }
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    mostrarDialog = false,
                    dialogError = null
                )
            }
        }
    }

    fun eliminarUsuario(usuarioId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // SIMULACIÓN: Aquí irá la llamada a Firebase
            delay(500)

            _usuarios.value = _usuarios.value.filter { it.id != usuarioId }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    data class AdminUsuariosUiState(
        val isLoading: Boolean = false,
        val mostrarDialog: Boolean = false,
        val usuarioEditando: UsuarioPreview? = null,
        val dialogNombre: String = "",
        val dialogEmail: String = "",
        val dialogPassword: String = "",
        val dialogRol: String = "USER",
        val dialogError: String? = null
    )
}

// Función de extensión para actualizar StateFlow
private fun <T> MutableStateFlow<T>.update(block: (T) -> T) {
    value = block(value)
}


 */
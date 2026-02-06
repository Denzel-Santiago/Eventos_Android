//features/login/presentation/viewmodel/AdminUsuariosViewModel.kt
package com.proyecto.eventos.features.login.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.features.login.domain.entities.UsuarioEntidad
import com.proyecto.eventos.features.login.domain.usecases.DeleteUsuarioUseCase
import com.proyecto.eventos.features.login.domain.usecases.GetUsuariosUseCase
import com.proyecto.eventos.features.login.domain.usecases.UpdateUsuarioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminUsuariosViewModel(
    private val getUsuariosUseCase: GetUsuariosUseCase,
    private val updateUsuarioUseCase: UpdateUsuarioUseCase,
    private val deleteUsuarioUseCase: DeleteUsuarioUseCase
) : ViewModel() {

    private val _usuarios = MutableStateFlow<List<UsuarioEntidad>>(emptyList())
    val usuarios = _usuarios.asStateFlow()

    var mostrarFormulario by mutableStateOf(false)
        private set

    var usuarioActual by mutableStateOf<UsuarioEntidad?>(null)
        private set

    var username by mutableStateOf("")
    var email by mutableStateOf("")

    init {
        Log.d("AdminUsuariosViewModel", "🔵 ViewModel inicializado")
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        Log.d("AdminUsuariosViewModel", "🔵 Cargando usuarios...")
        viewModelScope.launch {
            try {
                val listaUsuarios = getUsuariosUseCase.execute()
                Log.d("AdminUsuariosViewModel", "✅ Usuarios obtenidos: ${listaUsuarios.size}")
                _usuarios.value = listaUsuarios
            } catch (e: Exception) {
                Log.e("AdminUsuariosViewModel", "❌ ERROR: ${e.message}", e)
                _usuarios.value = emptyList()
            }
        }
    }

    fun editarUsuario(usuario: UsuarioEntidad) {
        usuarioActual = usuario
        username = usuario.username
        email = usuario.email
        mostrarFormulario = true
    }

    fun guardarUsuario() {
        viewModelScope.launch {
            usuarioActual?.let { usuario ->
                updateUsuarioUseCase.execute(usuario.id, username, email)
                cargarUsuarios()
                cerrarFormulario()
            }
        }
    }

    fun eliminarUsuario(id: Int) {
        viewModelScope.launch {
            deleteUsuarioUseCase.execute(id)
            cargarUsuarios()
        }
    }

    fun cerrarFormulario() {
        mostrarFormulario = false
        usuarioActual = null
        username = ""
        email = ""
    }

    fun onUsernameChange(value: String) {
        username = value
    }

    fun onEmailChange(value: String) {
        email = value
    }
}
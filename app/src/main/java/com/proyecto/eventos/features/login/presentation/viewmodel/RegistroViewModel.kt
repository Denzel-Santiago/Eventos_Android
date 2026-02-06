package com.proyecto.eventos.features.login.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.core.network.SessionManager
import com.proyecto.eventos.features.login.domain.usecases.RegistroUseCase
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val registroUseCase: RegistroUseCase
) : ViewModel() {

    var username by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    fun onUsernameChange(value: String) {
        username = value
    }

    fun onEmailChange(value: String) {
        email = value
    }

    fun onPasswordChange(value: String) {
        password = value
    }

    fun registro(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                mensajeError = "Complete todos los campos"
                return@launch
            }

            if (!email.contains("@")) {
                mensajeError = "Email inválido"
                return@launch
            }

            val usuario = registroUseCase.execute(username, email, password)
            if (usuario != null) {
                SessionManager.iniciarSesion(usuario)
                mensajeError = null
                onSuccess()
            } else {
                mensajeError = "Error al registrar usuario"
            }
        }
    }
}
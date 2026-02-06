//features/login/presentation/viewmodel/LoginViewModel.kt
package com.proyecto.eventos.features.login.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.core.network.SessionManager
import com.proyecto.eventos.features.login.domain.usecases.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    fun onUsernameChange(value: String) {
        username = value
    }

    fun onPasswordChange(value: String) {
        password = value
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                mensajeError = "Complete todos los campos"
                return@launch
            }

            val usuario = loginUseCase.execute(username, password)
            if (usuario != null) {
                SessionManager.iniciarSesion(usuario)
                mensajeError = null
                onSuccess()
            } else {
                mensajeError = "Credenciales incorrectas"
            }
        }
    }

    fun limpiarError() {
        mensajeError = null
    }
}
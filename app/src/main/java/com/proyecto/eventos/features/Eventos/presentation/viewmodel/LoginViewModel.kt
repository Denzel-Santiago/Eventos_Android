package com.proyecto.eventos.features.Eventos.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var correo by mutableStateOf("")
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    fun onCorreoCambiado(nuevoCorreo: String) {
        correo = nuevoCorreo
    }

    fun login(onLoginExitoso: () -> Unit) {
        if (correo.isBlank()) {
            mensajeError = "El correo no puede estar vacío"
            return
        }

        // Login simulado (luego tu compañero conecta la API)
        if (correo.contains("@")) {
            mensajeError = null
            onLoginExitoso()
        } else {
            mensajeError = "Correo no válido"
        }
    }
}

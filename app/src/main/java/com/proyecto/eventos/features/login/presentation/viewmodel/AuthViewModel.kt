// features/auth/presentation/viewmodel/AuthViewModel.kt
package com.proyecto.eventos.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    // Estados para Login
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    // Estados para Registro
    private val _registroState = MutableStateFlow(RegistroState())
    val registroState: StateFlow<RegistroState> = _registroState.asStateFlow()

    // Acciones de Login
    fun onLoginUsernameChange(username: String) {
        _loginState.update {
            it.copy(
                username = username,
                errorMessage = null
            )
        }
    }

    fun onLoginPasswordChange(password: String) {
        _loginState.update {
            it.copy(
                password = password,
                errorMessage = null
            )
        }
    }

    fun login() {
        val currentState = _loginState.value

        // Validaciones
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _loginState.update {
                it.copy(errorMessage = "Usuario y contraseña son requeridos")
            }
            return
        }

        viewModelScope.launch {
            // Iniciar loading
            _loginState.update { it.copy(isLoading = true, errorMessage = null) }

            // SIMULACIÓN: Aquí irá la llamada real a Firebase Auth
            delay(1500)

            // Simular éxito/error según credenciales
            if (currentState.username.contains("admin", ignoreCase = true)) {
                // Login exitoso como admin
                _loginState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        userRole = "ADMIN"
                    )
                }
            } else if (currentState.username.isNotBlank() && currentState.password.length >= 3) {
                // Login exitoso como usuario normal
                _loginState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        userRole = "USER"
                    )
                }
            } else {
                // Error
                _loginState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Credenciales inválidas",
                        isSuccess = false
                    )
                }
            }
        }
    }

    fun resetLoginState() {
        _loginState.update { LoginState() }
    }

    // Acciones de Registro
    fun onRegistroNombreChange(nombre: String) {
        _registroState.update {
            it.copy(
                nombreCompleto = nombre,
                errorMessage = null
            )
        }
    }

    fun onRegistroEmailChange(email: String) {
        _registroState.update {
            it.copy(
                email = email,
                errorMessage = null
            )
        }
    }

    fun onRegistroPasswordChange(password: String) {
        _registroState.update {
            it.copy(
                password = password,
                errorMessage = null
            )
        }
    }

    fun registro() {
        val currentState = _registroState.value

        // Validaciones
        when {
            currentState.nombreCompleto.isBlank() -> {
                _registroState.update { it.copy(errorMessage = "El nombre completo es requerido") }
                return
            }
            currentState.email.isBlank() -> {
                _registroState.update { it.copy(errorMessage = "El email es requerido") }
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches() -> {
                _registroState.update { it.copy(errorMessage = "Email no válido") }
                return
            }
            currentState.password.length < 6 -> {
                _registroState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
                return
            }
        }

        viewModelScope.launch {
            _registroState.update { it.copy(isLoading = true, errorMessage = null) }

            // SIMULACIÓN: Aquí irá la llamada real a Firebase Auth
            delay(1500)

            // Simular éxito
            _registroState.update {
                it.copy(
                    isLoading = false,
                    isSuccess = true
                )
            }
        }
    }

    fun resetRegistroState() {
        _registroState.update { RegistroState() }
    }

    // Data classes para los estados
    data class LoginState(
        val username: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val errorMessage: String? = null,
        val userRole: String = "USER"
    )

    data class RegistroState(
        val nombreCompleto: String = "",
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val errorMessage: String? = null
    )
}

// Función de extensión para actualizar StateFlow
private fun <T> MutableStateFlow<T>.update(block: (T) -> T) {
    value = block(value)
}
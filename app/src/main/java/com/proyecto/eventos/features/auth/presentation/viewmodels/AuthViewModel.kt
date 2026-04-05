package com.proyecto.eventos.features.auth.presentation.viewmodels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.features.auth.domain.usecases.GetPerfilUseCase
import com.proyecto.eventos.features.auth.domain.usecases.LoginUseCase
import com.proyecto.eventos.features.auth.domain.usecases.RegistroUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registroUseCase: RegistroUseCase,
    private val getPerfilUseCase: GetPerfilUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registroState = MutableStateFlow(RegistroState())
    val registroState: StateFlow<RegistroState> = _registroState.asStateFlow()

    // Login
    fun onLoginUsernameChange(value: String) {
        _loginState.value = _loginState.value.copy(username = value, errorMessage = null)
    }

    fun onLoginPasswordChange(value: String) {
        _loginState.value = _loginState.value.copy(password = value, errorMessage = null)
    }

    fun login() {
        val state = _loginState.value

        if (state.username.isBlank() || state.password.isBlank()) {
            _loginState.value = state.copy(errorMessage = "Correo y contraseña son requeridos")
            return
        }

        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true, errorMessage = null)

            val result = loginUseCase(state.username.trim(), state.password)

            result.fold(
                onSuccess = { usuario ->
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        userRole = usuario.rol
                    )
                },
                onFailure = { error ->
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = "Credenciales incorrectas. Verifica tu correo y contraseña."
                    )
                }
            )
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState()
    }

    // Registro
    fun onRegistroNombreChange(value: String) {
        _registroState.value = _registroState.value.copy(nombreCompleto = value, errorMessage = null)
    }

    fun onRegistroEmailChange(value: String) {
        _registroState.value = _registroState.value.copy(email = value, errorMessage = null)
    }

    fun onRegistroPasswordChange(value: String) {
        _registroState.value = _registroState.value.copy(password = value, errorMessage = null)
    }

    fun registro() {
        val state = _registroState.value

        when {
            state.nombreCompleto.isBlank() -> {
                _registroState.value = state.copy(errorMessage = "El nombre es requerido")
                return
            }
            state.email.isBlank() -> {
                _registroState.value = state.copy(errorMessage = "El correo es requerido")
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> {
                _registroState.value = state.copy(errorMessage = "Correo no válido")
                return
            }
            state.password.length < 6 -> {
                _registroState.value = state.copy(errorMessage = "La contraseña debe tener mínimo 6 caracteres")
                return
            }
        }

        viewModelScope.launch {
            _registroState.value = _registroState.value.copy(isLoading = true, errorMessage = null)

            val result = registroUseCase(
                nombre = state.nombreCompleto.trim(),
                email = state.email.trim(),
                password = state.password
            )

            result.fold(
                onSuccess = {
                    _registroState.value = _registroState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                },
                onFailure = { error ->
                    val mensaje = when {
                        error.message?.contains("email address is already in use") == true ->
                            "Este correo ya está registrado"
                        error.message?.contains("badly formatted") == true ->
                            "Formato de correo inválido"
                        else -> "Error al registrar. Intenta de nuevo."
                    }
                    _registroState.value = _registroState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = mensaje
                    )
                }
            )
        }
    }

    fun resetRegistroState() {
        _registroState.value = RegistroState()
    }

    data class LoginState(
        val username: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val errorMessage: String? = null,
        val userRole: String = "usuario"
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
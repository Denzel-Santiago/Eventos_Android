//com.proyecto.eventos.features.auth.presentation.viewmodels.LoginViewModel.kt
package com.proyecto.eventos.features.auth.presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.features.auth.domain.usecases.RegistroUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val registroUseCase: RegistroUseCase
) : ViewModel() {

    private val _nombre = mutableStateOf("")
    val nombre: State<String> = _nombre

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> = _confirmPassword

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _isSuccess = mutableStateOf(false)
    val isSuccess: State<Boolean> = _isSuccess

    fun onNombreChange(v: String) { _nombre.value = v }
    fun onEmailChange(v: String) { _email.value = v }
    fun onPasswordChange(v: String) { _password.value = v }
    fun onConfirmPasswordChange(v: String) { _confirmPassword.value = v }

    fun registrar() {
        if (_nombre.value.isBlank() || _email.value.isBlank() || _password.value.isBlank()) {
            _error.value = "Por favor, completa todos los campos"
            return
        }

        if (_password.value != _confirmPassword.value) {
            _error.value = "Las contraseñas no coinciden"
            return
        }

        if (_password.value.length < 6) {
            _error.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            registroUseCase(_nombre.value.trim(), _email.value.trim(), _password.value).fold(
                onSuccess = {
                    _isSuccess.value = true
                },
                onFailure = {
                    _error.value = "Error al registrar: ${it.message}"
                }
            )
            _isLoading.value = false
        }
    }
}

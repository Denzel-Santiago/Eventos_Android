package com.proyecto.eventos.features.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.proyecto.eventos.features.login.data.repositories.AuthRepository
import com.proyecto.eventos.features.login.domain.usecases.LoginUseCase

class LoginViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val repository = AuthRepository()
            val useCase = LoginUseCase(repository)
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
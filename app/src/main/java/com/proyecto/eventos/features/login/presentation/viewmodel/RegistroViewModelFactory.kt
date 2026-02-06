//features/login/presentation/viewmodel/RegistroViewModel.kt
package com.proyecto.eventos.features.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.proyecto.eventos.features.login.data.repositories.AuthRepository
import com.proyecto.eventos.features.login.domain.usecases.RegistroUseCase

class RegistroViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
            val repository = AuthRepository()
            val useCase = RegistroUseCase(repository)
            @Suppress("UNCHECKED_CAST")
            return RegistroViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
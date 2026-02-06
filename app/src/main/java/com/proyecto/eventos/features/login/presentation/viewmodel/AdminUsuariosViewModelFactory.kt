//features/login/presentation/viewmodel/AdminUsuariosViewModelFactory.kt
package com.proyecto.eventos.features.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.proyecto.eventos.features.login.data.repositories.AuthRepository
import com.proyecto.eventos.features.login.domain.usecases.DeleteUsuarioUseCase
import com.proyecto.eventos.features.login.domain.usecases.GetUsuariosUseCase
import com.proyecto.eventos.features.login.domain.usecases.UpdateUsuarioUseCase

class AdminUsuariosViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminUsuariosViewModel::class.java)) {
            val repository = AuthRepository()
            val getUseCase = GetUsuariosUseCase(repository)
            val updateUseCase = UpdateUsuarioUseCase(repository)
            val deleteUseCase = DeleteUsuarioUseCase(repository)
            @Suppress("UNCHECKED_CAST")
            return AdminUsuariosViewModel(getUseCase, updateUseCase, deleteUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
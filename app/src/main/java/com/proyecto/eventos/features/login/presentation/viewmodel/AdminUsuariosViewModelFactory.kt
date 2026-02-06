package com.proyecto.eventos.features.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.proyecto.eventos.features.login.data.repositories.AuthRepository

class AdminUsuariosViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminUsuariosViewModel::class.java)) {
            val repository = AuthRepository()
            @Suppress("UNCHECKED_CAST")
            return AdminUsuariosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
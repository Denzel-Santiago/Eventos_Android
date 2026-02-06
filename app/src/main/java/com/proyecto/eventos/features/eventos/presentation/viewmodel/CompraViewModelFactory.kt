//features/Eventos/presentation/viewmodel/CompraViewModel.kt
package com.proyecto.eventos.features.eventos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.proyecto.eventos.features.eventos.data.repositories.EventosRepository
import com.proyecto.eventos.features.eventos.domain.usecases.GetEventosUseCase

class CompraViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CompraViewModel::class.java)) {
            val repository = EventosRepository()
            val useCase = GetEventosUseCase(repository)
            @Suppress("UNCHECKED_CAST")
            return CompraViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
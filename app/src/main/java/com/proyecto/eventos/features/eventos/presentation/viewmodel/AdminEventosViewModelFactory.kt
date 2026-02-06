package com.proyecto.eventos.features.eventos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.proyecto.eventos.features.eventos.data.repositories.EventosRepository
import com.proyecto.eventos.features.eventos.domain.usecases.CreateEventoUseCase
import com.proyecto.eventos.features.eventos.domain.usecases.DeleteEventoUseCase
import com.proyecto.eventos.features.eventos.domain.usecases.GetEventosUseCase
import com.proyecto.eventos.features.eventos.domain.usecases.UpdateEventoUseCase

class AdminEventosViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminEventosViewModel::class.java)) {
            val repository = EventosRepository()
            val getUseCase = GetEventosUseCase(repository)
            val createUseCase = CreateEventoUseCase(repository)
            val updateUseCase = UpdateEventoUseCase(repository)
            val deleteUseCase = DeleteEventoUseCase(repository)
            @Suppress("UNCHECKED_CAST")
            return AdminEventosViewModel(getUseCase, createUseCase, updateUseCase, deleteUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.proyecto.eventos.features.notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.eventos.features.notifications.data.local.NotificationDao
import com.proyecto.eventos.features.notifications.data.local.NotificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationDao: NotificationDao
) : ViewModel() {

    val notificaciones: StateFlow<List<NotificationEntity>> =
        notificationDao.getNotificaciones()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val noLeidasCount: StateFlow<Int> =
        notificationDao.getNoLeidasCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    fun marcarLeida(id: String) {
        viewModelScope.launch {
            notificationDao.marcarLeida(id)
        }
    }

    fun marcarTodasLeidas() {
        viewModelScope.launch {
            notificationDao.marcarTodasLeidas()
        }
    }

    fun limpiarTodas() {
        viewModelScope.launch {
            notificationDao.limpiarTodas()
        }
    }
}
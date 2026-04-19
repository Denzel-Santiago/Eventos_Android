package com.proyecto.eventos.features.notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.eventos.features.notifications.data.local.NotificationDao
import com.proyecto.eventos.features.notifications.data.local.NotificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationDao: NotificationDao,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val currentUid = firebaseAuth.currentUser?.uid ?: ""

    val notificaciones: StateFlow<List<NotificationEntity>> =
        notificationDao.getNotificacionesPorUsuario(currentUid)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val noLeidasCount: StateFlow<Int> =
        notificationDao.getNoLeidasCount(currentUid)
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
            if (currentUid.isNotEmpty()) {
                notificationDao.marcarTodasLeidas(currentUid)
            }
        }
    }

    fun limpiarTodas() {
        viewModelScope.launch {
            if (currentUid.isNotEmpty()) {
                notificationDao.limpiarTodas(currentUid)
            }
        }
    }
}
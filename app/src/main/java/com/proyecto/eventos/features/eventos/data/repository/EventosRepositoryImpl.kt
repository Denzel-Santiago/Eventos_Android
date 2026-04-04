package com.proyecto.eventos.features.eventos.data.repository

import com.proyecto.eventos.core.network.NetworkMonitor
import com.proyecto.eventos.features.eventos.data.local.EventosLocalDataSource
import com.proyecto.eventos.features.eventos.data.remote.FirebaseEventosDataSource
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.eventos.domain.repositories.EventosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class EventosRepositoryImpl @Inject constructor(
    private val localDataSource: EventosLocalDataSource,
    private val remoteDataSource: FirebaseEventosDataSource,
    private val networkMonitor: NetworkMonitor
) : EventosRepository {

    override fun getEventos(): Flow<List<EventoEntidad>> {
        // Sincronizar con Firebase si hay internet
        if (networkMonitor.isConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    remoteDataSource.getEventosUnaVez().fold(
                        onSuccess = { eventos ->
                            localDataSource.guardarTodos(eventos)
                        },
                        onFailure = { }
                    )
                } catch (e: Exception) { }
            }
        }
        // Siempre devolver Flow de Room
        return localDataSource.getEventos()
    }

    override suspend fun createEvento(evento: EventoEntidad): Result<Unit> {
        return remoteDataSource.createEvento(evento)
    }

    override suspend fun updateEvento(evento: EventoEntidad): Result<Unit> {
        return remoteDataSource.updateEvento(evento)
    }

    override suspend fun deleteEvento(eventoId: String): Result<Unit> {
        return remoteDataSource.deleteEvento(eventoId)
    }

    override suspend fun restarStock(eventoId: String): Result<Unit> {
        // Restar en Room inmediatamente (sin esperar Firebase)
        localDataSource.restarStockLocal(eventoId)
        // Restar en Firebase si hay internet
        return if (networkMonitor.isConnected()) {
            remoteDataSource.restarStock(eventoId)
        } else {
            Result.success(Unit)
        }
    }

    override suspend fun tieneStock(eventoId: String): Result<Boolean> {
        return if (networkMonitor.isConnected()) {
            remoteDataSource.tieneStock(eventoId)
        } else {
            // Sin internet confiar en Room
            Result.success(true)
        }
    }
}

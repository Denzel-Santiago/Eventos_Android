//com.proyecto.eventos.features.eventos.domain.repositories.EventosRepository.kt
package com.proyecto.eventos.features.eventos.domain.repositories

import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import kotlinx.coroutines.flow.Flow

interface EventosRepository {
    fun getEventos(): Flow<List<EventoEntidad>>
    suspend fun createEvento(evento: EventoEntidad): Result<Unit>
    suspend fun updateEvento(evento: EventoEntidad): Result<Unit>
    suspend fun deleteEvento(eventoId: String): Result<Unit>
    suspend fun restarStock(eventoId: String): Result<Unit>
    suspend fun tieneStock(eventoId: String): Result<Boolean>
}

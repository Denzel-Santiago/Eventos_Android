//features/Eventos/data/repositories/EventosRepository.kt
package com.proyecto.eventos.features.eventos.data.repositories

import com.proyecto.eventos.core.network.RetrofitClient
import com.proyecto.eventos.features.eventos.data.remote.CreateEventoRequest
import com.proyecto.eventos.features.eventos.data.remote.toEntity
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad

class EventosRepository {

    private val api = RetrofitClient.eventosApi

    suspend fun obtenerEventos(): List<EventoEntidad> {
        return try {
            val response = api.getEventos()
            response.map { it.toEntity() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun crearEvento(evento: EventoEntidad): Boolean {
        return try {
            val request = CreateEventoRequest(
                name = evento.nombre,
                location = evento.ubicacion,
                date = evento.fecha,
                availableTickets = evento.boletosDisponibles,
                price = evento.precio
            )
            api.createEvento(request)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun actualizarEvento(evento: EventoEntidad): Boolean {
        return try {
            val request = CreateEventoRequest(
                name = evento.nombre,
                location = evento.ubicacion,
                date = evento.fecha,
                availableTickets = evento.boletosDisponibles,
                price = evento.precio
            )
            api.updateEvento(evento.id, request)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarEvento(id: Int): Boolean {
        return try {
            api.deleteEvento(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}
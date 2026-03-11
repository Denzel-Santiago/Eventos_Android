package com.proyecto.eventos.features.favoritos.domain.repositories

import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import kotlinx.coroutines.flow.Flow

interface FavoritosRepository {
    fun getFavoritos(): Flow<List<EventoEntidad>>
    suspend fun agregarFavorito(evento: EventoEntidad)
    suspend fun eliminarFavorito(eventoId: String)
    fun esFavorito(eventoId: String): Flow<Boolean>
}

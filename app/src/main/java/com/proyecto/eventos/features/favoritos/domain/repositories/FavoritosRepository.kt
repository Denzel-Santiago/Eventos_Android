//com.proyecto.eventos.features.favoritos.domain.repositories.FavoritosRepository
package com.proyecto.eventos.features.favoritos.domain.repositories

import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import kotlinx.coroutines.flow.Flow

interface FavoritosRepository {
    fun getFavoritos(uid: String): Flow<List<EventoEntidad>>
    suspend fun agregarFavorito(uid: String, evento: EventoEntidad)
    suspend fun eliminarFavorito(uid: String, eventoId: String)
    fun esFavorito(uid: String, eventoId: String): Flow<Boolean>
}
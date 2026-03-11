package com.proyecto.eventos.features.favoritos.domain.usecases

import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.favoritos.domain.repositories.FavoritosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritosUseCase @Inject constructor(
    private val repository: FavoritosRepository
) {
    operator fun invoke(): Flow<List<EventoEntidad>> = repository.getFavoritos()
}

package com.proyecto.eventos.features.favoritos.domain.usecases

import com.proyecto.eventos.features.favoritos.domain.repositories.FavoritosRepository
import javax.inject.Inject

class EliminarFavoritoUseCase @Inject constructor(
    private val repository: FavoritosRepository
) {
    suspend operator fun invoke(eventoId: String) = repository.eliminarFavorito(eventoId)
}

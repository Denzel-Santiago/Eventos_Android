//com.proyecto.eventos.features.favoritos.domain.usecases.EliminarFavoritoUseCase
package com.proyecto.eventos.features.favoritos.domain.usecases

import com.proyecto.eventos.features.favoritos.domain.repositories.FavoritosRepository
import javax.inject.Inject

class EliminarFavoritoUseCase @Inject constructor(
    private val repository: FavoritosRepository
) {
    suspend operator fun invoke(uid: String, eventoId: String) =
        repository.eliminarFavorito(uid, eventoId)
}
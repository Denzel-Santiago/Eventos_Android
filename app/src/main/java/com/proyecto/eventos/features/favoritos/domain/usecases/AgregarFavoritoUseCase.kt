//com.proyecto.eventos.features.favoritos.domain.usecases.AgregarFavoritoUseCase
package com.proyecto.eventos.features.favoritos.domain.usecases

import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.favoritos.domain.repositories.FavoritosRepository
import javax.inject.Inject

class AgregarFavoritoUseCase @Inject constructor(
    private val repository: FavoritosRepository
) {
    suspend operator fun invoke(uid: String, evento: EventoEntidad) =
        repository.agregarFavorito(uid, evento)
}
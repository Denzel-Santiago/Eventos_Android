package com.proyecto.eventos.features.favoritos.data.local

import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.favoritos.domain.repositories.FavoritosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritosLocalDataSource @Inject constructor(
    private val dao: FavoritoDao
) : FavoritosRepository {

    override fun getFavoritos(): Flow<List<EventoEntidad>> {
        return dao.getFavoritos().map { lista ->
            lista.map { entity ->
                EventoEntidad(
                    id = entity.eventoId,
                    nombre = entity.nombre,
                    fecha = entity.fecha,
                    hora = entity.hora,
                    ubicacion = entity.ubicacion,
                    precio = entity.precio,
                    stock = entity.stock,
                    imagen = entity.imagen
                )
            }
        }
    }

    override suspend fun agregarFavorito(evento: EventoEntidad) {
        dao.insertar(
            FavoritoEntity(
                eventoId = evento.id,
                nombre = evento.nombre,
                fecha = evento.fecha,
                hora = evento.hora,
                ubicacion = evento.ubicacion,
                precio = evento.precio,
                stock = evento.stock,
                imagen = evento.imagen
            )
        )
    }

    override suspend fun eliminarFavorito(eventoId: String) {
        dao.eliminar(eventoId)
    }

    override fun esFavorito(eventoId: String): Flow<Boolean> {
        return dao.esFavorito(eventoId)
    }
}

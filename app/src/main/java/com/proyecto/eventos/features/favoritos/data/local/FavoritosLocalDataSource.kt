//com.proyecto.eventos.features.favoritos.data.local.FavoritosLocalDataSource
package com.proyecto.eventos.features.favoritos.data.local

import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.favoritos.domain.repositories.FavoritosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritosLocalDataSource @Inject constructor(
    private val dao: FavoritoDao
) : FavoritosRepository {

    override fun getFavoritos(uid: String): Flow<List<EventoEntidad>> {
        return dao.getFavoritos(uid).map { lista ->
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

    override suspend fun agregarFavorito(uid: String, evento: EventoEntidad) {
        dao.insertar(
            FavoritoEntity(
                id = "${uid}_${evento.id}",
                uid = uid,
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

    override suspend fun eliminarFavorito(uid: String, eventoId: String) {
        dao.eliminar(uid, eventoId)
    }

    override fun esFavorito(uid: String, eventoId: String): Flow<Boolean> {
        return dao.esFavorito(uid, eventoId)
    }
}
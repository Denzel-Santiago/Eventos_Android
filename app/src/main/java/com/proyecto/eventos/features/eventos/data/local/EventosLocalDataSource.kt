package com.proyecto.eventos.features.eventos.data.local

import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventosLocalDataSource @Inject constructor(
    private val dao: EventoDao
) {
    fun getEventos(): Flow<List<EventoEntidad>> {
        return dao.getEventos().map { lista ->
            lista.map { entity ->
                EventoEntidad(
                    id        = entity.id,
                    nombre    = entity.nombre,
                    fecha     = entity.fecha,
                    hora      = entity.hora,
                    ubicacion = entity.ubicacion,
                    precio    = entity.precio,
                    stock     = entity.stock,
                    imagen    = entity.imagen
                )
            }
        }
    }

    suspend fun guardarTodos(eventos: List<EventoEntidad>) {
        dao.insertarTodos(
            eventos.map { evento ->
                EventoCacheEntity(
                    id        = evento.id,
                    nombre    = evento.nombre,
                    fecha     = evento.fecha,
                    hora      = evento.hora,
                    ubicacion = evento.ubicacion,
                    precio    = evento.precio,
                    stock     = evento.stock,
                    imagen    = evento.imagen
                )
            }
        )
    }

    suspend fun tieneDatos(): Boolean = dao.count() > 0

    suspend fun restarStockLocal(eventoId: String) {
        dao.restarStockLocal(eventoId)
    }
}

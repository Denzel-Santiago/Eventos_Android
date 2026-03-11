//com.proyecto.eventos.features.eventos.data.remote.FirebaseEventosDataSource.kt
package com.proyecto.eventos.features.eventos.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.eventos.domain.repositories.EventosRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseEventosDataSource @Inject constructor(
    private val dbRef: DatabaseReference
) : EventosRepository {

    override fun getEventos(): Flow<List<EventoEntidad>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eventos = snapshot.children.mapNotNull { child ->
                    child.getValue(EventoDTO::class.java)?.let { dto ->
                        EventoEntidad(
                            id = child.key ?: "",
                            nombre = dto.nombre,
                            fecha = dto.fecha,
                            hora = dto.hora,
                            ubicacion = dto.ubicacion,
                            precio = dto.precio,
                            stock = dto.stock,
                            imagen = dto.imagen
                        )
                    }
                }
                trySend(eventos)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        dbRef.child("eventos").addValueEventListener(listener)
        awaitClose { dbRef.child("eventos").removeEventListener(listener) }
    }

    override suspend fun createEvento(evento: EventoEntidad): Result<Unit> {
        return try {
            val key = dbRef.child("eventos").push().key ?: throw Exception("Error al generar ID")
            val dto = EventoDTO(
                id = key,
                nombre = evento.nombre,
                fecha = evento.fecha,
                hora = evento.hora,
                ubicacion = evento.ubicacion,
                precio = evento.precio,
                stock = evento.stock,
                imagen = evento.imagen
            )
            dbRef.child("eventos").child(key).setValue(dto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEvento(evento: EventoEntidad): Result<Unit> {
        return try {
            val dto = EventoDTO(
                id = evento.id,
                nombre = evento.nombre,
                fecha = evento.fecha,
                hora = evento.hora,
                ubicacion = evento.ubicacion,
                precio = evento.precio,
                stock = evento.stock,
                imagen = evento.imagen
            )
            dbRef.child("eventos").child(evento.id).setValue(dto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteEvento(eventoId: String): Result<Unit> {
        return try {
            dbRef.child("eventos").child(eventoId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

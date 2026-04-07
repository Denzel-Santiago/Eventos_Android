//com.proyecto.eventos.features.eventos.data.remote.FirebaseEventosDataSource.kt
package com.proyecto.eventos.features.eventos.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.suspendCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseEventosDataSource @Inject constructor(
    private val dbRef: DatabaseReference
) {

    fun getEventos(): Flow<List<EventoEntidad>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eventos = snapshot.children.mapNotNull { child ->
                    child.getValue(EventoDTO::class.java)?.let { dto ->
                        EventoEntidad(
                            id        = child.key ?: "",
                            nombre    = dto.nombre,
                            fecha     = dto.fecha,
                            hora      = dto.hora,
                            ubicacion = dto.ubicacion,
                            precio    = dto.precio,
                            stock     = dto.stock,
                            imagen    = dto.imagen
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

    suspend fun createEvento(evento: EventoEntidad): Result<Unit> {
        return try {
            val key = dbRef.child("eventos").push().key
                ?: throw Exception("Error al generar ID")
            val dto = EventoDTO(
                id        = key,
                nombre    = evento.nombre,
                fecha     = evento.fecha,
                hora      = evento.hora,
                ubicacion = evento.ubicacion,
                precio    = evento.precio,
                stock     = evento.stock,
                imagen    = evento.imagen
            )
            dbRef.child("eventos").child(key).setValue(dto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEvento(evento: EventoEntidad): Result<Unit> {
        return try {
            val dto = EventoDTO(
                id        = evento.id,
                nombre    = evento.nombre,
                fecha     = evento.fecha,
                hora      = evento.hora,
                ubicacion = evento.ubicacion,
                precio    = evento.precio,
                stock     = evento.stock,
                imagen    = evento.imagen
            )
            dbRef.child("eventos").child(evento.id).setValue(dto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEvento(eventoId: String): Result<Unit> {
        return try {
            dbRef.child("eventos").child(eventoId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restarStock(eventoId: String): Result<Unit> {
        return suspendCoroutine { continuation ->
            val stockRef = dbRef
                .child("eventos")
                .child(eventoId)
                .child("stock")

            stockRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(
                    currentData: MutableData
                ): Transaction.Result {

                    val stockActual = currentData.getValue(Int::class.java)
                        ?: return Transaction.success(currentData)  

                    if (stockActual <= 0) {
                        return Transaction.abort()
                    }
                    currentData.value = stockActual - 1
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    snapshot: DataSnapshot?
                ) {
                    when {
                        error != null -> continuation.resumeWithException(
                            Exception("Error Firebase: ${error.message}")
                        )
                        !committed -> {

                            val stockFinal = snapshot
                                ?.getValue(Int::class.java) ?: 0
                            if (stockFinal <= 0) {
                                continuation.resumeWithException(
                                    Exception("Stock agotado")
                                )
                            } else {

                                continuation.resume(Result.success(Unit))
                            }
                        }
                        else -> continuation.resume(Result.success(Unit))
                    }
                }
            })
        }
    }

    suspend fun tieneStock(eventoId: String): Result<Boolean> {
        return try {
            val snapshot = dbRef
                .child("eventos")
                .child(eventoId)
                .child("stock")
                .get()
                .await()
            val stock = snapshot.getValue(Int::class.java) ?: 0
            Result.success(stock > 0)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventosUnaVez(): Result<List<EventoEntidad>> {
        return try {
            val snapshot = dbRef.child("eventos").get().await()
            val eventos = snapshot.children.mapNotNull { child ->
                child.getValue(EventoDTO::class.java)?.let { dto ->
                    EventoEntidad(
                        id        = child.key ?: "",
                        nombre    = dto.nombre,
                        fecha     = dto.fecha,
                        hora      = dto.hora,
                        ubicacion = dto.ubicacion,
                        precio    = dto.precio,
                        stock     = dto.stock,
                        imagen    = dto.imagen
                    )
                }
            }
            Result.success(eventos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
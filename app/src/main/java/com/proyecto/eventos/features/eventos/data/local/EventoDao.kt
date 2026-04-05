//com.proyecto.eventos.features.eventos.data.local.EventoDao.kt
package com.proyecto.eventos.features.eventos.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {

    @Query("SELECT * FROM eventos_cache ORDER BY fecha ASC")
    fun getEventos(): Flow<List<EventoCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(eventos: List<EventoCacheEntity>)

    @Query("DELETE FROM eventos_cache")
    suspend fun limpiarTodos()

    @Query("SELECT COUNT(*) FROM eventos_cache")
    suspend fun count(): Int

    @Query("""
        UPDATE eventos_cache 
        SET stock = stock - 1 
        WHERE id = :eventoId AND stock > 0
    """)
    suspend fun restarStockLocal(eventoId: String)
}

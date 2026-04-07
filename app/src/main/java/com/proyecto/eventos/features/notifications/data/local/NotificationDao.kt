//com.proyecto.eventos.features.notifications.data.local.NotificationDao.kt
package com.proyecto.eventos.features.notifications.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notificaciones ORDER BY timestamp DESC")
    fun getNotificaciones(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(notificacion: NotificationEntity)

    @Query("UPDATE notificaciones SET leida = 1 WHERE id = :id")
    suspend fun marcarLeida(id: String)

    @Query("UPDATE notificaciones SET leida = 1")
    suspend fun marcarTodasLeidas()

    @Query("SELECT COUNT(*) FROM notificaciones WHERE leida = 0")
    fun getNoLeidasCount(): Flow<Int>

    @Query("DELETE FROM notificaciones")
    suspend fun limpiarTodas()
}
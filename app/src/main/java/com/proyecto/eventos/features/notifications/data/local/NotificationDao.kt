//com.proyecto.eventos.features.notifications.data.local.NotificationDao.kt
package com.proyecto.eventos.features.notifications.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notificaciones WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificacionesPorUsuario(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(notificacion: NotificationEntity)

    @Query("UPDATE notificaciones SET leida = 1 WHERE id = :id")
    suspend fun marcarLeida(id: String)

    @Query("UPDATE notificaciones SET leida = 1 WHERE userId = :userId")
    suspend fun marcarTodasLeidas(userId: String)

    @Query("SELECT COUNT(*) FROM notificaciones WHERE userId = :userId AND leida = 0")
    fun getNoLeidasCount(userId: String): Flow<Int>

    @Query("DELETE FROM notificaciones WHERE userId = :userId")
    suspend fun limpiarTodas(userId: String)
}
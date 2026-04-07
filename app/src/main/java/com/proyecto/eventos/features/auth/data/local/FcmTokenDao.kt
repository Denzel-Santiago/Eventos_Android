//com.proyecto.eventos.features.auth.data.local.FcmTokenDao.kt
package com.proyecto.eventos.features.auth.data.local

import androidx.room.*

@Dao
interface FcmTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarToken(token: FcmTokenEntity)

    @Query("SELECT * FROM fcm_tokens WHERE uid = :uid LIMIT 1")
    suspend fun getToken(uid: String): FcmTokenEntity?

    @Query("DELETE FROM fcm_tokens WHERE uid = :uid")
    suspend fun eliminarToken(uid: String)
}
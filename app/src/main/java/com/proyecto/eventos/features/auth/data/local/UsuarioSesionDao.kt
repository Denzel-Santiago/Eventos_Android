//com.proyecto.eventos.features.auth.data.local.UsuarioLocalEntity.kt
package com.proyecto.eventos.features.auth.data.local

import androidx.room.*

@Dao
interface UsuarioSesionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarSesion(usuario: UsuarioLocalEntity)

    @Query("SELECT * FROM usuario_sesion LIMIT 1")
    suspend fun getSesionActual(): UsuarioLocalEntity?

    @Query("DELETE FROM usuario_sesion")
    suspend fun cerrarSesion()
}

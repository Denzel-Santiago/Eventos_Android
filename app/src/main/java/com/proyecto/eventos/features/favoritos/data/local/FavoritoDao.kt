//com.proyecto.eventos.features.favoritos.data.local.FavoritoDao
package com.proyecto.eventos.features.favoritos.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {

    @Query("SELECT * FROM favoritos WHERE uid = :uid")
    fun getFavoritos(uid: String): Flow<List<FavoritoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(favorito: FavoritoEntity)

    @Query("DELETE FROM favoritos WHERE uid = :uid AND eventoId = :eventoId")
    suspend fun eliminar(uid: String, eventoId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos WHERE uid = :uid AND eventoId = :eventoId)")
    fun esFavorito(uid: String, eventoId: String): Flow<Boolean>
}
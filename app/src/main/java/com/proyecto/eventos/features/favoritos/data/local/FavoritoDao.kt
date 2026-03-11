package com.proyecto.eventos.features.favoritos.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {

    @Query("SELECT * FROM favoritos")
    fun getFavoritos(): Flow<List<FavoritoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(favorito: FavoritoEntity)

    @Query("DELETE FROM favoritos WHERE eventoId = :eventoId")
    suspend fun eliminar(eventoId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos WHERE eventoId = :eventoId)")
    fun esFavorito(eventoId: String): Flow<Boolean>
}

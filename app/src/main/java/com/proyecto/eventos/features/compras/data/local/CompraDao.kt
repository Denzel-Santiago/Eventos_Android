package com.proyecto.eventos.features.compras.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {

    @Query("SELECT * FROM historial_compras WHERE uid = :uid ORDER BY timestamp DESC")
    fun getHistorial(uid: String): Flow<List<CompraLocalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(compra: CompraLocalEntity)

    @Query("DELETE FROM historial_compras WHERE uid = :uid")
    suspend fun limpiarHistorial(uid: String)

    @Query("SELECT * FROM historial_compras WHERE sincronizado = 0 AND uid = :uid")
    suspend fun getComprasPendientesPorUsuario(uid: String): List<CompraLocalEntity>

    @Query("UPDATE historial_compras SET sincronizado = 1 WHERE id = :compraId")
    suspend fun marcarSincronizada(compraId: String)
}

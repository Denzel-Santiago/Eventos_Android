package com.proyecto.eventos.features.compras.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {

    @Query("SELECT * FROM historial_compras ORDER BY timestamp DESC")
    fun getHistorial(): Flow<List<CompraLocalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(compra: CompraLocalEntity)

    @Query("DELETE FROM historial_compras")
    suspend fun limpiarHistorial()
}

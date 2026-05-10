package com.vote4tech.domicilio2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vote4tech.domicilio2.data.local.entity.EleccionLocalEntity

@Dao
interface EleccionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(elecciones: List<EleccionLocalEntity>)

    @Query("SELECT * FROM elecciones WHERE estado = 'EN_CURSO'")
    suspend fun obtenerActivas(): List<EleccionLocalEntity>

    @Query("DELETE FROM elecciones")
    suspend fun limpiarTodas()
}

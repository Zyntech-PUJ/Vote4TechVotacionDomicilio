package com.vote4tech.domicilio2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vote4tech.domicilio2.data.local.entity.CiudadanoLocalEntity

@Dao
interface CiudadanoLocalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(ciudadanos: List<CiudadanoLocalEntity>)

    @Query("SELECT * FROM ciudadanos WHERE cedula = :cedula AND habilitadoDomicilio = 1")
    suspend fun buscarHabilitado(cedula: String): CiudadanoLocalEntity?

    @Query("DELETE FROM ciudadanos")
    suspend fun limpiarTodos()
}

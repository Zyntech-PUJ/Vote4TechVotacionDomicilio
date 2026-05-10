package com.vote4tech.domicilio2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vote4tech.domicilio2.data.local.entity.CandidatoLocalEntity

@Dao
interface CandidatoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(candidatos: List<CandidatoLocalEntity>)

    @Query("SELECT * FROM candidatos WHERE idEleccion = :idEleccion")
    suspend fun obtenerPorEleccion(idEleccion: Long): List<CandidatoLocalEntity>

    @Query("DELETE FROM candidatos")
    suspend fun limpiarTodos()
}

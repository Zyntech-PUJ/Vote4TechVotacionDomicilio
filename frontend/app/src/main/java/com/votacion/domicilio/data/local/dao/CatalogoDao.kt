package com.votacion.domicilio.data.local.dao

import androidx.room.*
import com.votacion.domicilio.data.local.entity.CandidatoLocalEntity
import com.votacion.domicilio.data.local.entity.CiudadanoLocalEntity
import com.votacion.domicilio.data.local.entity.EleccionLocalEntity

@Dao
interface EleccionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(elecciones: List<EleccionLocalEntity>)

    @Query("SELECT * FROM eleccion_local WHERE estado = 'EN_CURSO'")
    suspend fun obtenerActivas(): List<EleccionLocalEntity>

    @Query("SELECT COUNT(*) FROM eleccion_local")
    suspend fun contar(): Int

    @Query("DELETE FROM eleccion_local")
    suspend fun limpiar()
}

@Dao
interface CandidatoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(candidatos: List<CandidatoLocalEntity>)

    @Query("SELECT * FROM candidato_local WHERE idEleccion = :idEleccion")
    suspend fun obtenerPorEleccion(idEleccion: Long): List<CandidatoLocalEntity>

    @Query("DELETE FROM candidato_local")
    suspend fun limpiar()
}

@Dao
interface CiudadanoLocalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(ciudadanos: List<CiudadanoLocalEntity>)

    @Query("SELECT * FROM ciudadano_local WHERE cedula = :cedula LIMIT 1")
    suspend fun buscarPorCedula(cedula: String): CiudadanoLocalEntity?

    @Query("SELECT COUNT(*) FROM ciudadano_local")
    suspend fun contar(): Int

    @Query("DELETE FROM ciudadano_local")
    suspend fun limpiar()
}

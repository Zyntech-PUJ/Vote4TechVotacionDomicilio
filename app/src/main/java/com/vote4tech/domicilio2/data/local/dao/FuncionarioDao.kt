package com.vote4tech.domicilio2.data.local.dao

import androidx.room.*
import com.vote4tech.domicilio2.data.local.entity.FuncionarioLocalEntity

@Dao
interface FuncionarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(list: List<FuncionarioLocalEntity>)

    @Query("DELETE FROM funcionarios")
    suspend fun limpiarTodos()

    @Query("SELECT * FROM funcionarios WHERE cedula = :cedula AND password = :password AND activo = 1 LIMIT 1")
    suspend fun autenticar(cedula: String, password: String): FuncionarioLocalEntity?
}

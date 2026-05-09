package com.votacion.domicilio.data.local.dao

import androidx.room.*
import com.votacion.domicilio.data.local.entity.VotoDraftEntity

@Dao
interface VotoDraftDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(draft: VotoDraftEntity)

    @Update
    suspend fun actualizar(draft: VotoDraftEntity)

    @Query("SELECT * FROM voto_draft WHERE estado != 'GUARDADO' LIMIT 1")
    suspend fun obtenerPendiente(): VotoDraftEntity?

    @Query("UPDATE voto_draft SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: String, estado: String)

    @Query("DELETE FROM voto_draft WHERE id = :id")
    suspend fun eliminar(id: String)
}

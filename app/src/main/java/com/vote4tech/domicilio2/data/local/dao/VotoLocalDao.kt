package com.vote4tech.domicilio2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vote4tech.domicilio2.data.local.entity.EstadoVoto
import com.vote4tech.domicilio2.data.local.entity.VotoLocalEntity

@Dao
interface VotoLocalDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(voto: VotoLocalEntity)

    @Query("SELECT COUNT(*) FROM votos_locales WHERE cedula = :cedula AND idEleccion = :idEleccion")
    suspend fun yaVoto(cedula: String, idEleccion: Long): Int

    @Query("SELECT * FROM votos_locales WHERE estado = 'PENDIENTE'")
    suspend fun obtenerPendientes(): List<VotoLocalEntity>

    @Query("UPDATE votos_locales SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: String, estado: EstadoVoto)

    @Query("SELECT COUNT(*) FROM votos_locales WHERE estado = 'PENDIENTE'")
    suspend fun contarPendientes(): Int

    @Query("SELECT COUNT(*) FROM votos_locales WHERE estado = 'SINCRONIZADO'")
    suspend fun contarSincronizados(): Int
}

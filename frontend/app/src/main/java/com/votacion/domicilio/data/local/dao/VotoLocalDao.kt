package com.votacion.domicilio.data.local.dao

import androidx.room.*
import com.votacion.domicilio.data.local.entity.VotoLocalEntity

@Dao
interface VotoLocalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(voto: VotoLocalEntity): Long

    @Query("SELECT * FROM voto_local WHERE estado = 'PENDIENTE'")
    suspend fun obtenerPendientes(): List<VotoLocalEntity>

    @Query("SELECT COUNT(*) FROM voto_local WHERE estado = 'PENDIENTE'")
    suspend fun contarPendientes(): Int

    @Query("SELECT COUNT(*) FROM voto_local WHERE estado = 'SINCRONIZADO'")
    suspend fun contarSincronizados(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM voto_local WHERE cedula = :cedula AND idEleccion = :idEleccion)")
    suspend fun yaVoto(cedula: String, idEleccion: Long): Boolean

    @Query("UPDATE voto_local SET estado = 'SINCRONIZADO', sincronizadoEn = :timestamp WHERE id = :id")
    suspend fun marcarSincronizado(id: String, timestamp: Long)
}

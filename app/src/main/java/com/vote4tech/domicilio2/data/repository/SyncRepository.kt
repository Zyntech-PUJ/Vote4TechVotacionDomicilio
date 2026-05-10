package com.vote4tech.domicilio2.data.repository

import com.vote4tech.domicilio2.data.local.dao.CandidatoDao
import com.vote4tech.domicilio2.data.local.dao.CiudadanoLocalDao
import com.vote4tech.domicilio2.data.local.dao.EleccionDao
import com.vote4tech.domicilio2.data.local.dao.VotoLocalDao
import com.vote4tech.domicilio2.data.local.entity.CandidatoLocalEntity
import com.vote4tech.domicilio2.data.local.entity.CiudadanoLocalEntity
import com.vote4tech.domicilio2.data.local.entity.EleccionLocalEntity
import com.vote4tech.domicilio2.data.local.entity.EstadoVoto
import com.vote4tech.domicilio2.data.remote.ApiClients
import com.vote4tech.domicilio2.data.remote.dto.VotoCouchDto

sealed class SyncResult {
    data class Exito(val descargados: Int, val subidos: Int) : SyncResult()
    data class Error(val mensaje: String) : SyncResult()
}

class SyncRepository(
    private val api: ApiClients,
    private val eleccionDao: EleccionDao,
    private val candidatoDao: CandidatoDao,
    private val ciudadanoDao: CiudadanoLocalDao,
    private val votoDao: VotoLocalDao
) {
    /** Descarga elecciones, candidatos y ciudadanos habilitados desde el servidor central */
    suspend fun descargarDatos(): SyncResult {
        return try {
            val elecciones = api.centralApi.getEleccionesActivas()
            val ciudadanos = api.centralApi.getCiudadanosDomicilio()

            val eleccionesEntities = elecciones.map {
                EleccionLocalEntity(it.id, it.nombre, it.descripcion, it.estado)
            }
            val ciudadanosEntities = ciudadanos.map {
                CiudadanoLocalEntity(it.cedula, it.nombre, it.habilitadoDomicilio)
            }

            // Descargar candidatos de todas las elecciones
            val candidatos = mutableListOf<CandidatoLocalEntity>()
            for (e in elecciones) {
                val cands = api.centralApi.getCandidatos(e.id)
                candidatos.addAll(cands.map {
                    CandidatoLocalEntity(it.id, it.nombre, it.partido, it.numero.toIntOrNull() ?: 0, e.id)
                })
            }

            eleccionDao.limpiarTodas()
            candidatoDao.limpiarTodos()
            ciudadanoDao.limpiarTodos()

            eleccionDao.insertarTodas(eleccionesEntities)
            candidatoDao.insertarTodos(candidatos)
            ciudadanoDao.insertarTodos(ciudadanosEntities)

            SyncResult.Exito(descargados = elecciones.size + ciudadanos.size + candidatos.size, subidos = 0)
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Error desconocido al descargar")
        }
    }

    /** Sube votos PENDIENTE a CouchDB y los marca como SINCRONIZADO */
    suspend fun subirVotos(): SyncResult {
        return try {
            val pendientes = votoDao.obtenerPendientes()
            var subidos = 0
            for (voto in pendientes) {
                val dto = VotoCouchDto(
                    _id = voto.id,
                    cedula = voto.cedula,
                    idEleccion = voto.idEleccion,
                    idCandidato = voto.idCandidato,
                    idFuncionario = voto.idFuncionario,
                    timestamp = voto.timestamp
                )
                api.couchDbApi.guardarVoto(voto.id, dto)
                votoDao.actualizarEstado(voto.id, EstadoVoto.SINCRONIZADO)
                subidos++
            }
            SyncResult.Exito(descargados = 0, subidos = subidos)
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Error desconocido al subir votos")
        }
    }
}

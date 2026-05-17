package com.vote4tech.domicilio2.data.repository

import com.vote4tech.domicilio2.data.local.dao.CandidatoDao
import com.vote4tech.domicilio2.data.local.dao.CiudadanoLocalDao
import com.vote4tech.domicilio2.data.local.dao.EleccionDao
import com.vote4tech.domicilio2.data.local.dao.FuncionarioDao
import com.vote4tech.domicilio2.data.local.dao.VotoLocalDao
import com.vote4tech.domicilio2.data.local.entity.CandidatoLocalEntity
import com.vote4tech.domicilio2.data.local.entity.CiudadanoLocalEntity
import com.vote4tech.domicilio2.data.local.entity.EleccionLocalEntity
import com.vote4tech.domicilio2.data.local.entity.EstadoVoto
import com.vote4tech.domicilio2.data.local.entity.FuncionarioLocalEntity
import com.vote4tech.domicilio2.data.remote.ApiClients
import com.vote4tech.domicilio2.data.remote.dto.VotoCouchDto
import com.vote4tech.domicilio2.util.PrefsManager

sealed class SyncResult {
    data class Exito(val descargados: Int, val subidos: Int) : SyncResult()
    data class Error(val mensaje: String) : SyncResult()
}

class SyncRepository(
    private val api: ApiClients,
    private val prefs: PrefsManager,
    private val eleccionDao: EleccionDao,
    private val candidatoDao: CandidatoDao,
    private val ciudadanoDao: CiudadanoLocalDao,
    private val votoDao: VotoLocalDao,
    private val funcionarioDao: FuncionarioDao
) {
    /** Descarga elecciones, candidatos, ciudadanos y funcionarios desde el servidor central */
    suspend fun descargarDatos(): SyncResult {
        return try {
            val elecciones = api.centralApi.getEleccionesActivas()
            val ciudadanos = api.centralApi.getCiudadanosTodos()
            val funcionarios = api.centralApi.getFuncionarios()

            val eleccionesEntities = elecciones.map {
                EleccionLocalEntity(it.id, it.nombre, it.descripcion, it.estado)
            }
            val ciudadanosEntities = ciudadanos.map {
                CiudadanoLocalEntity(it.cedula, it.nombre, it.habilitadoDomicilio, it.tipoDocumento, it.direccion)
            }
            val funcionariosEntities = funcionarios.map {
                FuncionarioLocalEntity(it.cedula, it.nombre, it.password, it.activo)
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
            funcionarioDao.limpiarTodos()

            eleccionDao.insertarTodas(eleccionesEntities)
            candidatoDao.insertarTodos(candidatos)
            ciudadanoDao.insertarTodos(ciudadanosEntities)
            funcionarioDao.insertarTodos(funcionariosEntities)

            prefs.ultimaDescarga = System.currentTimeMillis()

            SyncResult.Exito(descargados = elecciones.size + ciudadanos.size + candidatos.size + funcionarios.size, subidos = 0)
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
                    idSeleccion = voto.idCandidato,
                    idFuncionario = voto.idFuncionario,
                    timestamp = voto.timestamp
                )
                api.couchDbApi.guardarVoto(voto.id, dto)
                votoDao.actualizarEstado(voto.id, EstadoVoto.SINCRONIZADO)
                subidos++
            }
            prefs.ultimaSubida = System.currentTimeMillis()
            SyncResult.Exito(descargados = 0, subidos = subidos)
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Error desconocido al subir votos")
        }
    }

    /** Verifica si el servidor central es accesible */
    suspend fun pingServidor(): Boolean {
        return try {
            val response = api.centralApi.ping()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}


package com.votacion.domicilio.data.repository

import com.votacion.domicilio.data.local.dao.CandidatoDao
import com.votacion.domicilio.data.local.dao.CiudadanoLocalDao
import com.votacion.domicilio.data.local.dao.EleccionDao
import com.votacion.domicilio.data.local.dao.VotoLocalDao
import com.votacion.domicilio.data.local.entity.*
import com.votacion.domicilio.data.remote.ApiClients
import com.votacion.domicilio.data.remote.dto.VotoCouchDto
import java.time.LocalDateTime

/**
 * Coordina la sincronización bidireccional:
 * - PULL: descarga datos electorales del servidor central → Room
 * - PUSH: envía votos pendientes en Room → CouchDB central
 */
class SyncRepository(
    private val eleccionDao: EleccionDao,
    private val candidatoDao: CandidatoDao,
    private val ciudadanoLocalDao: CiudadanoLocalDao,
    private val votoLocalDao: VotoLocalDao
) {

    // ── PULL: Datos electorales ───────────────────────────────

    suspend fun sincronizarDatosElectorales(): SyncResult {
        return try {
            val respElecciones = ApiClients.centralApi.getEleccionesActivas()
            if (!respElecciones.isSuccessful || respElecciones.body() == null)
                return SyncResult.Error("No se pudieron obtener las elecciones: ${respElecciones.code()}")

            val elecciones = respElecciones.body()!!
            eleccionDao.limpiar()
            candidatoDao.limpiar()

            eleccionDao.insertarTodas(elecciones.map { e ->
                EleccionLocalEntity(
                    idEleccion = e.idEleccion,
                    nombre = e.nombre,
                    tipo = e.tipo,
                    estado = e.estado,
                    listaAbierta = e.listaAbierta
                )
            })

            // Candidatos: usar los incluidos en la respuesta o hacer fetch individual
            val candidatosEntidades = elecciones.flatMap { e ->
                (e.candidatos ?: emptyList()).map { c ->
                    CandidatoLocalEntity(
                        idCandidato = c.idCandidato,
                        nombre = c.nombre,
                        numero = c.numero,
                        fotoUrl = c.fotoUrl,
                        idEleccion = e.idEleccion,
                        idLista = c.idLista,
                        nombrePartido = c.nombrePartido,
                        siglaPartido = c.siglaPartido,
                        logoPartido = c.logoPartido
                    )
                }
            }
            candidatoDao.insertarTodos(candidatosEntidades)

            // Ciudadanos domiciliarios
            val respCiudadanos = ApiClients.centralApi.getCiudadanosDomicilio()
            if (respCiudadanos.isSuccessful && respCiudadanos.body() != null) {
                ciudadanoLocalDao.limpiar()
                ciudadanoLocalDao.insertarTodos(respCiudadanos.body()!!.map { c ->
                    CiudadanoLocalEntity(cedula = c.cedula, nombre = c.nombre, genero = c.genero)
                })
            }

            SyncResult.Exito(
                elecciones = elecciones.size,
                candidatos = candidatosEntidades.size
            )
        } catch (e: Exception) {
            SyncResult.Error("Error de red: ${e.message}")
        }
    }

    // ── PUSH: Votos a CouchDB ─────────────────────────────────

    suspend fun sincronizarVotos(): VotoSyncResult {
        val pendientes = votoLocalDao.obtenerPendientes()
        if (pendientes.isEmpty()) return VotoSyncResult(enviados = 0, errores = 0)

        var enviados = 0
        var errores = 0

        for (voto in pendientes) {
            try {
                val doc = VotoCouchDto(
                    _id = voto.id,
                    idEleccion = voto.idEleccion,
                    tipoSeleccion = voto.tipoSeleccion,
                    idSeleccion = voto.idSeleccion,
                    timestamp = LocalDateTime.now().toString()
                )
                val resp = ApiClients.couchDbApi.guardarVoto(voto.id, doc)
                if (resp.isSuccessful || resp.code() == 409) {
                    // 409 = ya existe en CouchDB (idempotente)
                    votoLocalDao.marcarSincronizado(voto.id, System.currentTimeMillis())
                    enviados++
                } else {
                    errores++
                }
            } catch (e: Exception) {
                errores++
            }
        }
        return VotoSyncResult(enviados, errores)
    }
}

sealed class SyncResult {
    data class Exito(val elecciones: Int, val candidatos: Int) : SyncResult()
    data class Error(val mensaje: String) : SyncResult()
}

data class VotoSyncResult(val enviados: Int, val errores: Int)

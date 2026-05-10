package com.vote4tech.domicilio2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vote4tech.domicilio2.data.local.dao.CandidatoDao
import com.vote4tech.domicilio2.data.local.dao.CiudadanoLocalDao
import com.vote4tech.domicilio2.data.local.dao.EleccionDao
import com.vote4tech.domicilio2.data.local.dao.VotoLocalDao
import com.vote4tech.domicilio2.data.local.entity.CandidatoLocalEntity
import com.vote4tech.domicilio2.data.local.entity.CiudadanoLocalEntity
import com.vote4tech.domicilio2.data.local.entity.EleccionLocalEntity
import com.vote4tech.domicilio2.data.local.entity.EstadoVoto
import com.vote4tech.domicilio2.data.local.entity.VotoLocalEntity
import com.vote4tech.domicilio2.data.repository.SyncRepository
import com.vote4tech.domicilio2.data.repository.SyncResult
import com.vote4tech.domicilio2.util.PrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed class DomicilioState {
    object Idle : DomicilioState()
    object Cargando : DomicilioState()
    data class CiudadanoIdentificado(val ciudadano: CiudadanoLocalEntity) : DomicilioState()
    data class CiudadanoNoEncontrado(val cedula: String) : DomicilioState()
    data class YaVoto(val cedula: String) : DomicilioState()
    data class EleccionesListas(val elecciones: List<EleccionLocalEntity>, val ciudadano: CiudadanoLocalEntity) : DomicilioState()
    data class CandidatosListos(
        val candidatos: List<CandidatoLocalEntity>,
        val eleccion: EleccionLocalEntity,
        val ciudadano: CiudadanoLocalEntity
    ) : DomicilioState()
    data class CandidatoSeleccionado(
        val candidato: CandidatoLocalEntity,
        val eleccion: EleccionLocalEntity,
        val ciudadano: CiudadanoLocalEntity
    ) : DomicilioState()
    data class VotoRegistrado(val cedula: String, val nombreEleccion: String) : DomicilioState()
    data class SincronizandoDescarga(val mensaje: String = "Descargando datos del servidor...") : DomicilioState()
    data class SincronizandoSubida(val mensaje: String = "Subiendo votos a CouchDB...") : DomicilioState()
    data class SyncExito(val mensaje: String) : DomicilioState()
    data class SyncError(val mensaje: String) : DomicilioState()
    data class Error(val mensaje: String) : DomicilioState()
}

class DomicilioViewModel(
    private val prefs: PrefsManager,
    private val syncRepository: SyncRepository,
    private val eleccionDao: EleccionDao,
    private val candidatoDao: CandidatoDao,
    private val ciudadanoDao: CiudadanoLocalDao,
    private val votoDao: VotoLocalDao
) : ViewModel() {

    private val _state = MutableStateFlow<DomicilioState>(DomicilioState.Idle)
    val state: StateFlow<DomicilioState> = _state

    fun identificarCiudadano(cedula: String) {
        viewModelScope.launch {
            _state.value = DomicilioState.Cargando
            val ciudadano = ciudadanoDao.buscarHabilitado(cedula)
            if (ciudadano == null) {
                _state.value = DomicilioState.CiudadanoNoEncontrado(cedula)
                return@launch
            }
            _state.value = DomicilioState.CiudadanoIdentificado(ciudadano)
        }
    }

    fun cargarElecciones(ciudadano: CiudadanoLocalEntity) {
        viewModelScope.launch {
            _state.value = DomicilioState.Cargando
            val elecciones = eleccionDao.obtenerActivas()
            if (elecciones.isEmpty()) {
                _state.value = DomicilioState.Error("No hay elecciones activas. Sincronice primero.")
                return@launch
            }
            _state.value = DomicilioState.EleccionesListas(elecciones, ciudadano)
        }
    }

    fun seleccionarEleccion(eleccion: EleccionLocalEntity, ciudadano: CiudadanoLocalEntity) {
        viewModelScope.launch {
            _state.value = DomicilioState.Cargando
            // Verificar si ya votó en esta elección
            val yaVoto = votoDao.yaVoto(ciudadano.cedula, eleccion.id) > 0
            if (yaVoto) {
                _state.value = DomicilioState.YaVoto(ciudadano.cedula)
                return@launch
            }
            val candidatos = candidatoDao.obtenerPorEleccion(eleccion.id)
            _state.value = DomicilioState.CandidatosListos(candidatos, eleccion, ciudadano)
        }
    }

    fun seleccionarCandidato(candidato: CandidatoLocalEntity, eleccion: EleccionLocalEntity, ciudadano: CiudadanoLocalEntity) {
        _state.value = DomicilioState.CandidatoSeleccionado(candidato, eleccion, ciudadano)
    }

    fun confirmarVoto(candidato: CandidatoLocalEntity, eleccion: EleccionLocalEntity, ciudadano: CiudadanoLocalEntity) {
        viewModelScope.launch {
            _state.value = DomicilioState.Cargando
            try {
                val voto = VotoLocalEntity(
                    id = UUID.randomUUID().toString(),
                    cedula = ciudadano.cedula,
                    idEleccion = eleccion.id,
                    idCandidato = candidato.id,
                    idFuncionario = prefs.idFuncionario,
                    timestamp = System.currentTimeMillis(),
                    estado = EstadoVoto.PENDIENTE
                )
                votoDao.insertar(voto)
                _state.value = DomicilioState.VotoRegistrado(ciudadano.cedula, eleccion.nombre)
            } catch (e: Exception) {
                _state.value = DomicilioState.Error("Error al registrar voto: ${e.message}")
            }
        }
    }

    fun descargarDatos() {
        viewModelScope.launch {
            _state.value = DomicilioState.SincronizandoDescarga()
            when (val result = syncRepository.descargarDatos()) {
                is SyncResult.Exito -> _state.value = DomicilioState.SyncExito(
                    "Descarga exitosa: ${result.descargados} registros actualizados"
                )
                is SyncResult.Error -> _state.value = DomicilioState.SyncError(result.mensaje)
            }
        }
    }

    fun subirVotos() {
        viewModelScope.launch {
            _state.value = DomicilioState.SincronizandoSubida()
            when (val result = syncRepository.subirVotos()) {
                is SyncResult.Exito -> _state.value = DomicilioState.SyncExito(
                    "Sincronización exitosa: ${result.subidos} votos enviados"
                )
                is SyncResult.Error -> _state.value = DomicilioState.SyncError(result.mensaje)
            }
        }
    }

    fun reiniciar() {
        _state.value = DomicilioState.Idle
    }

    fun limpiarError() {
        _state.value = DomicilioState.Idle
    }

    class Factory(
        private val prefs: PrefsManager,
        private val syncRepository: SyncRepository,
        private val eleccionDao: EleccionDao,
        private val candidatoDao: CandidatoDao,
        private val ciudadanoDao: CiudadanoLocalDao,
        private val votoDao: VotoLocalDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DomicilioViewModel(prefs, syncRepository, eleccionDao, candidatoDao, ciudadanoDao, votoDao) as T
        }
    }
}

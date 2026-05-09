package com.votacion.domicilio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.votacion.domicilio.data.local.dao.*
import com.votacion.domicilio.data.local.entity.*
import com.votacion.domicilio.data.remote.ApiClients
import com.votacion.domicilio.data.repository.SyncRepository
import com.votacion.domicilio.data.repository.SyncResult
import com.votacion.domicilio.util.PrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class DomicilioViewModel(
    private val prefs: PrefsManager,
    private val votoDraftDao: VotoDraftDao,
    private val votoLocalDao: VotoLocalDao,
    private val eleccionDao: EleccionDao,
    private val candidatoDao: CandidatoDao,
    private val ciudadanoLocalDao: CiudadanoLocalDao,
    private val syncRepo: SyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DomicilioUiState>(DomicilioUiState.Idle)
    val uiState: StateFlow<DomicilioUiState> = _uiState.asStateFlow()

    private val _elecciones = MutableStateFlow<List<EleccionLocalEntity>>(emptyList())
    val elecciones: StateFlow<List<EleccionLocalEntity>> = _elecciones.asStateFlow()

    private val _candidatos = MutableStateFlow<List<CandidatoLocalEntity>>(emptyList())
    val candidatos: StateFlow<List<CandidatoLocalEntity>> = _candidatos.asStateFlow()

    private val _pendientesCount = MutableStateFlow(0)
    val pendientesCount: StateFlow<Int> = _pendientesCount.asStateFlow()

    private var currentDraft: VotoDraftEntity? = null

    // ── Inicialización ─────────────────────────────────────────
    fun cargarEstado() {
        viewModelScope.launch {
            _pendientesCount.value = votoLocalDao.contarPendientes()
            val pendiente = votoDraftDao.obtenerPendiente()
            if (pendiente != null) {
                currentDraft = pendiente
                _uiState.value = DomicilioUiState.DraftPendiente(
                    EstadoDraft.valueOf(pendiente.estado)
                )
            }
        }
    }

    // ── Sync ───────────────────────────────────────────────────
    fun sincronizarDatos() {
        _uiState.value = DomicilioUiState.Sincronizando("Descargando datos electorales…")
        viewModelScope.launch {
            when (val r = syncRepo.sincronizarDatosElectorales()) {
                is SyncResult.Exito -> {
                    _uiState.value = DomicilioUiState.SyncExito(
                        "Descargados ${r.elecciones} elecciones y ${r.candidatos} candidatos."
                    )
                }
                is SyncResult.Error -> _uiState.value = DomicilioUiState.SyncError(r.mensaje)
            }
        }
    }

    fun sincronizarVotos() {
        _uiState.value = DomicilioUiState.Sincronizando("Enviando votos pendientes…")
        viewModelScope.launch {
            val r = syncRepo.sincronizarVotos()
            _pendientesCount.value = votoLocalDao.contarPendientes()
            _uiState.value = DomicilioUiState.SyncExito(
                "Enviados: ${r.enviados}. Errores: ${r.errores}."
            )
        }
    }

    // ── Votación ───────────────────────────────────────────────
    fun identificarCiudadano(cedula: String) {
        _uiState.value = DomicilioUiState.Cargando
        viewModelScope.launch {
            val ciudadano = ciudadanoLocalDao.buscarPorCedula(cedula)
            if (ciudadano == null) {
                _uiState.value = DomicilioUiState.Error("Cédula no encontrada en el padrón local.")
                return@launch
            }
            val draft = VotoDraftEntity(
                id = UUID.randomUUID().toString(),
                cedula = ciudadano.cedula,
                nombreCiudadano = ciudadano.nombre,
                idEleccion = 0L,
                nombreEleccion = "",
                estado = EstadoDraft.IDENTIFICADO.name
            )
            votoDraftDao.insertar(draft)
            currentDraft = draft
            _uiState.value = DomicilioUiState.CiudadanoIdentificado(ciudadano.nombre, ciudadano.cedula)
        }
    }

    fun cargarElecciones() {
        viewModelScope.launch {
            _elecciones.value = eleccionDao.obtenerActivas()
            _uiState.value = DomicilioUiState.EleccionesListas
        }
    }

    fun seleccionarEleccion(eleccion: EleccionLocalEntity) {
        viewModelScope.launch {
            currentDraft = currentDraft?.copy(
                idEleccion = eleccion.idEleccion,
                nombreEleccion = eleccion.nombre,
                estado = EstadoDraft.ELECCION_SELECCIONADA.name
            )
            currentDraft?.let { votoDraftDao.actualizar(it) }
            _candidatos.value = candidatoDao.obtenerPorEleccion(eleccion.idEleccion)
            _uiState.value = DomicilioUiState.CandidatosListos
        }
    }

    fun seleccionarCandidato(candidato: CandidatoLocalEntity) {
        viewModelScope.launch {
            currentDraft = currentDraft?.copy(
                tipoSeleccion = "CANDIDATO",
                idSeleccion = candidato.idCandidato,
                nombreSeleccion = candidato.nombre,
                estado = EstadoDraft.CANDIDATO_SELECCIONADO.name
            )
            currentDraft?.let { votoDraftDao.actualizar(it) }
            _uiState.value = DomicilioUiState.ListoParaConfirmar(
                candidato.nombre,
                candidato.nombrePartido ?: ""
            )
        }
    }

    fun confirmarVoto() {
        val draft = currentDraft ?: return
        viewModelScope.launch {
            // Verificar que no haya votado antes (control local)
            if (votoLocalDao.yaVoto(draft.cedula, draft.idEleccion)) {
                _uiState.value = DomicilioUiState.Error("Esta cédula ya registró un voto en esta elección.")
                return@launch
            }

            votoDraftDao.actualizarEstado(draft.id, EstadoDraft.CONFIRMADO.name)

            val voto = VotoLocalEntity(
                id = draft.id,
                cedula = draft.cedula,
                idEleccion = draft.idEleccion,
                tipoSeleccion = draft.tipoSeleccion ?: "CANDIDATO",
                idSeleccion = draft.idSeleccion ?: 0L,
                estado = EstadoVoto.PENDIENTE.name
            )

            val inserted = votoLocalDao.insertar(voto)
            if (inserted == -1L) {
                // IGNORE retorna -1 si duplicado (ya existe por cedula+eleccion)
                _uiState.value = DomicilioUiState.Error("Esta cédula ya registró un voto en esta elección.")
                return@launch
            }

            votoDraftDao.actualizarEstado(draft.id, EstadoDraft.GUARDADO.name)
            currentDraft = null
            _pendientesCount.value = votoLocalDao.contarPendientes()
            _uiState.value = DomicilioUiState.VotoGuardado
        }
    }

    fun reiniciar() {
        currentDraft = null
        _elecciones.value = emptyList()
        _candidatos.value = emptyList()
        _uiState.value = DomicilioUiState.Idle
    }

    fun limpiarError() { _uiState.value = DomicilioUiState.Idle }
}

// ── Estados ────────────────────────────────────────────────────
sealed class DomicilioUiState {
    data object Idle : DomicilioUiState()
    data object Cargando : DomicilioUiState()
    data object EleccionesListas : DomicilioUiState()
    data object CandidatosListos : DomicilioUiState()
    data object VotoGuardado : DomicilioUiState()
    data class DraftPendiente(val estado: EstadoDraft) : DomicilioUiState()
    data class CiudadanoIdentificado(val nombre: String, val cedula: String) : DomicilioUiState()
    data class ListoParaConfirmar(val nombreCandidato: String, val nombrePartido: String) : DomicilioUiState()
    data class Sincronizando(val mensaje: String) : DomicilioUiState()
    data class SyncExito(val mensaje: String) : DomicilioUiState()
    data class SyncError(val mensaje: String) : DomicilioUiState()
    data class Error(val mensaje: String) : DomicilioUiState()
}

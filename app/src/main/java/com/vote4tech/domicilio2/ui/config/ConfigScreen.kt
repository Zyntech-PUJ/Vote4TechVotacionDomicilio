package com.vote4tech.domicilio2.ui.config

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vote4tech.domicilio2.ui.ConfigInfo
import com.vote4tech.domicilio2.ui.DomicilioState
import com.vote4tech.domicilio2.ui.DomicilioViewModel
import java.text.DateFormat
import java.util.Date

private fun formatTimestamp(ts: Long): String {
    if (ts == 0L) return "—"
    return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date(ts))
}

@Composable
fun ConfigScreen(
    viewModel: DomicilioViewModel,
    onConsultasClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val configInfo by viewModel.configInfo.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarEstadoConfig()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Configuración",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Status panel
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🔄 Sincronización central",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { viewModel.cargarEstadoConfig() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar estado")
                    }
                }
                if (configInfo != null) {
                    val ci = configInfo!!
                    Text("Sistema central: ${if (ci.serverAccesible) "🟢 Accesible" else "🔴 No accesible"}")
                    Text("Última descarga: ${formatTimestamp(ci.ultimaDescarga)}")
                    Text("Última subida: ${formatTimestamp(ci.ultimaSubida)}")
                    Text("Votos domicilio locales: ${ci.votosLocales}")
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Text("Cargando estado...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        HorizontalDivider()

        Text("Sincronización", style = MaterialTheme.typography.titleMedium)

        when (state) {
            is DomicilioState.SincronizandoDescarga -> {
                CircularProgressIndicator()
                Text((state as DomicilioState.SincronizandoDescarga).mensaje)
            }
            is DomicilioState.SincronizandoSubida -> {
                CircularProgressIndicator()
                Text((state as DomicilioState.SincronizandoSubida).mensaje)
            }
            is DomicilioState.SyncExito -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = (state as DomicilioState.SyncExito).mensaje,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Button(
                    onClick = { viewModel.limpiarError() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aceptar")
                }
            }
            is DomicilioState.SyncError -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Error: ${(state as DomicilioState.SyncError).mensaje}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Button(
                    onClick = { viewModel.limpiarError() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aceptar")
                }
            }
            else -> {}
        }

        val isLoading = state is DomicilioState.SincronizandoDescarga ||
                state is DomicilioState.SincronizandoSubida

        Button(
            onClick = { viewModel.descargarDatos() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Descargar datos del servidor")
        }

        Button(
            onClick = { viewModel.subirVotos() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Subir votos a CouchDB")
        }

        HorizontalDivider()

        Text("Consultas", style = MaterialTheme.typography.titleMedium)

        OutlinedButton(
            onClick = onConsultasClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Consultas de jurado")
        }
    }
}


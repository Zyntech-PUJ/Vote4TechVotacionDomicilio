package com.votacion.domicilio.ui.sync

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.votacion.domicilio.ui.DomicilioUiState
import com.votacion.domicilio.ui.DomicilioViewModel

@Composable
fun SyncScreen(
    viewModel: DomicilioViewModel,
    onIniciarVotacion: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val pendientes by viewModel.pendientesCount.collectAsState()

    LaunchedEffect(Unit) { viewModel.cargarEstado() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Panel de Sincronización", style = MaterialTheme.typography.headlineSmall)

        // Votos pendientes
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Votos pendientes de enviar", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "$pendientes votos en cola",
                    style = MaterialTheme.typography.displaySmall,
                    color = if (pendientes > 0) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        when (val s = uiState) {
            is DomicilioUiState.Sincronizando ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text(s.mensaje)
                }
            is DomicilioUiState.SyncExito ->
                Text(s.mensaje, color = MaterialTheme.colorScheme.primary)
            is DomicilioUiState.SyncError ->
                Text(s.mensaje, color = MaterialTheme.colorScheme.error)
            else -> {}
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { viewModel.sincronizarDatos() },
                enabled = uiState !is DomicilioUiState.Sincronizando
            ) { Text("↓ Descargar datos") }

            OutlinedButton(
                onClick = { viewModel.sincronizarVotos() },
                enabled = uiState !is DomicilioUiState.Sincronizando && pendientes > 0
            ) { Text("↑ Enviar votos") }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onIniciarVotacion,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Iniciar votación") }
    }
}

package com.vote4tech.domicilio2.ui.sync

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vote4tech.domicilio2.ui.DomicilioState
import com.vote4tech.domicilio2.ui.DomicilioViewModel

@Composable
fun SyncScreen(viewModel: DomicilioViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sincronización", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

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
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Text(
                        text = (state as DomicilioState.SyncExito).mensaje,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Button(onClick = { viewModel.limpiarError() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Aceptar")
                }
            }
            is DomicilioState.SyncError -> {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(
                        text = "Error: ${(state as DomicilioState.SyncError).mensaje}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Button(onClick = { viewModel.limpiarError() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Aceptar")
                }
            }
            else -> {}
        }

        val isLoading = state is DomicilioState.SincronizandoDescarga || state is DomicilioState.SincronizandoSubida

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
    }
}

package com.votacion.domicilio.ui.eleccion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.votacion.domicilio.data.local.entity.EleccionLocalEntity
import com.votacion.domicilio.ui.DomicilioUiState
import com.votacion.domicilio.ui.DomicilioViewModel

@Composable
fun EleccionScreen(
    viewModel: DomicilioViewModel,
    onEleccionSeleccionada: () -> Unit
) {
    val uiState   by viewModel.uiState.collectAsState()
    val elecciones by viewModel.elecciones.collectAsState()

    LaunchedEffect(Unit) { viewModel.cargarElecciones() }

    LaunchedEffect(uiState) {
        if (uiState is DomicilioUiState.CandidatosListos) onEleccionSeleccionada()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Seleccione una elección", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        if (elecciones.isEmpty()) {
            Text(
                "No hay elecciones activas. Descargue datos desde el Panel de Sync.",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(elecciones) { eleccion ->
                    EleccionItem(eleccion) { viewModel.seleccionarEleccion(eleccion) }
                }
            }
        }
    }
}

@Composable
private fun EleccionItem(eleccion: EleccionLocalEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(eleccion.nombre, style = MaterialTheme.typography.titleMedium)
            Text(eleccion.tipo, style = MaterialTheme.typography.bodySmall)
        }
    }
}

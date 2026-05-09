package com.votacion.domicilio.ui.votacion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.votacion.domicilio.data.local.entity.CandidatoLocalEntity
import com.votacion.domicilio.ui.DomicilioUiState
import com.votacion.domicilio.ui.DomicilioViewModel

@Composable
fun VotacionScreen(
    viewModel: DomicilioViewModel,
    onCandidatoSeleccionado: () -> Unit
) {
    val uiState   by viewModel.uiState.collectAsState()
    val candidatos by viewModel.candidatos.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is DomicilioUiState.ListoParaConfirmar) onCandidatoSeleccionado()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Seleccione su candidato", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        if (candidatos.isEmpty()) {
            CircularProgressIndicator()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(candidatos) { candidato ->
                    CandidatoItem(candidato) { viewModel.seleccionarCandidato(candidato) }
                }
            }
        }
    }
}

@Composable
private fun CandidatoItem(candidato: CandidatoLocalEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(candidato.nombre, style = MaterialTheme.typography.titleMedium)
                if (!candidato.nombrePartido.isNullOrBlank())
                    Text(
                        "${candidato.siglaPartido ?: ""} — ${candidato.nombrePartido}",
                        style = MaterialTheme.typography.bodySmall
                    )
            }
            Text(
                "No. ${candidato.numero}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

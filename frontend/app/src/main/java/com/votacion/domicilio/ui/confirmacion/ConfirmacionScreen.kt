package com.votacion.domicilio.ui.confirmacion

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.votacion.domicilio.ui.DomicilioUiState
import com.votacion.domicilio.ui.DomicilioViewModel

@Composable
fun ConfirmacionScreen(
    viewModel: DomicilioViewModel,
    onVotoGuardado: () -> Unit,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is DomicilioUiState.VotoGuardado) {
            viewModel.reiniciar()
            onVotoGuardado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val s = uiState) {
            is DomicilioUiState.ListoParaConfirmar -> {
                Text("Confirme su voto", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(24.dp))

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Candidato", style = MaterialTheme.typography.labelSmall)
                        Text(s.nombreCandidato, style = MaterialTheme.typography.titleLarge)
                        if (s.nombrePartido.isNotBlank()) {
                            Text("Partido: ${s.nombrePartido}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.confirmarVoto() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Confirmar voto") }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Cambiar selección") }
            }

            is DomicilioUiState.Cargando -> CircularProgressIndicator()

            is DomicilioUiState.Error -> {
                Text(
                    s.mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.limpiarError(); onVolver() }) {
                    Text("Volver")
                }
            }

            is DomicilioUiState.VotoGuardado -> {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                Text("Voto guardado", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Se enviará al servidor cuando haya conexión.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            else -> {}
        }
    }
}

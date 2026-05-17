package com.vote4tech.domicilio2.ui.consultas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vote4tech.domicilio2.data.local.entity.CiudadanoLocalEntity
import com.vote4tech.domicilio2.ui.DomicilioState
import com.vote4tech.domicilio2.ui.DomicilioViewModel

@Composable
fun ConsultasScreen(
    viewModel: DomicilioViewModel,
    onAtras: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var filtro by remember { mutableStateOf("") }
    var ciudadanos by remember { mutableStateOf<List<CiudadanoLocalEntity>>(emptyList()) }

    LaunchedEffect(state) {
        if (state is DomicilioState.ConsultasCiudadanosListos) {
            ciudadanos = (state as DomicilioState.ConsultasCiudadanosListos).ciudadanos
        }
    }

    LaunchedEffect(filtro) {
        viewModel.buscarCiudadanosDomicilio(filtro)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Ciudadanos - Voto Domiciliario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = {
                viewModel.limpiarConsultas()
                onAtras()
            }) { Text("Salir") }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = filtro,
            onValueChange = { filtro = it },
            label = { Text("Buscar por nombre o cédula") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("${ciudadanos.size} ciudadanos encontrados",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ciudadanos) { c ->
                CiudadanoCard(c)
            }
        }
    }
}

@Composable
private fun CiudadanoCard(ciudadano: CiudadanoLocalEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(ciudadano.nombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${ciudadano.tipoDocumento}: ${ciudadano.cedula}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!ciudadano.direccion.isNullOrBlank()) {
                Text("Dir: ${ciudadano.direccion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            SuggestionChip(
                onClick = {},
                label = { Text(if (ciudadano.habilitadoDomicilio) "Domicilio" else "Urna") },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (ciudadano.habilitadoDomicilio)
                        MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    }
}

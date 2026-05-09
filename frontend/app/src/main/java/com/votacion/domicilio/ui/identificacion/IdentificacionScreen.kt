package com.votacion.domicilio.ui.identificacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.votacion.domicilio.ui.DomicilioUiState
import com.votacion.domicilio.ui.DomicilioViewModel

@Composable
fun IdentificacionScreen(
    viewModel: DomicilioViewModel,
    onCiudadanoIdentificado: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var cedula by remember { mutableStateOf("") }

    // Navegar cuando el ciudadano fue identificado
    LaunchedEffect(uiState) {
        if (uiState is DomicilioUiState.CiudadanoIdentificado) onCiudadanoIdentificado()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Identificación", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = cedula,
            onValueChange = { cedula = it },
            label = { Text("Número de cédula") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { if (cedula.isNotBlank()) viewModel.identificarCiudadano(cedula.trim()) }
            )
        )

        if (uiState is DomicilioUiState.Error) {
            Spacer(Modifier.height(8.dp))
            Text(
                (uiState as DomicilioUiState.Error).mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.identificarCiudadano(cedula.trim()) },
            modifier = Modifier.fillMaxWidth(),
            enabled = cedula.isNotBlank() && uiState !is DomicilioUiState.Cargando
        ) {
            if (uiState is DomicilioUiState.Cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Verificar cédula")
            }
        }
    }
}

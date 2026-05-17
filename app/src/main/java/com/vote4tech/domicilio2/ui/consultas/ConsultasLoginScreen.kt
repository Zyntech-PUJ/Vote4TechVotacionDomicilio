package com.vote4tech.domicilio2.ui.consultas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.vote4tech.domicilio2.ui.DomicilioState
import com.vote4tech.domicilio2.ui.DomicilioViewModel

@Composable
fun ConsultasLoginScreen(
    viewModel: DomicilioViewModel,
    onLoginExito: () -> Unit,
    onAtras: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var cedula by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is DomicilioState.ConsultasLoginExito) {
            viewModel.buscarCiudadanosDomicilio()
            onLoginExito()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Consultas - Funcionario", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Ingresa tus credenciales de funcionario", style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = cedula,
            onValueChange = { cedula = it.filter { c -> c.isDigit() } },
            label = { Text("Cédula del funcionario") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state is DomicilioState.ConsultasLoginError) {
            Text(
                (state as DomicilioState.ConsultasLoginError).mensaje,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (state is DomicilioState.Cargando) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.loginFuncionario(cedula, password) },
                modifier = Modifier.fillMaxWidth(),
                enabled = cedula.isNotBlank() && password.isNotBlank()
            ) {
                Text("Iniciar sesión")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(onClick = onAtras, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}

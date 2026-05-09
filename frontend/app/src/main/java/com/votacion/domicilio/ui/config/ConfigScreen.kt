package com.votacion.domicilio.ui.config

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.votacion.domicilio.data.remote.ApiClients
import com.votacion.domicilio.util.PrefsManager

@Composable
fun ConfigScreen(onConfigurado: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { PrefsManager(context) }

    var centralUrl   by remember { mutableStateOf(prefs.centralApiUrl) }
    var couchUrl     by remember { mutableStateOf(prefs.couchDbUrl) }
    var couchUser    by remember { mutableStateOf(prefs.couchDbUser) }
    var couchPass    by remember { mutableStateOf(prefs.couchDbPassword) }
    var idFuncionario by remember { mutableStateOf(prefs.idFuncionario) }
    var error        by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Configuración Domicilio", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = centralUrl,
            onValueChange = { centralUrl = it },
            label = { Text("URL Servidor Central (ej: http://10.0.0.1:8080)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = couchUrl,
            onValueChange = { couchUrl = it },
            label = { Text("URL CouchDB Central (ej: http://10.0.0.1:5984/votos_domicilio)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )
        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = couchUser,
                onValueChange = { couchUser = it },
                label = { Text("Usuario CouchDB") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = couchPass,
                onValueChange = { couchPass = it },
                label = { Text("Contraseña") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
        }
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = idFuncionario,
            onValueChange = { idFuncionario = it },
            label = { Text("ID Funcionario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (error.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                if (centralUrl.isBlank() || couchUrl.isBlank()) {
                    error = "Los campos de URL son obligatorios."
                    return@Button
                }
                prefs.centralApiUrl  = centralUrl.trim()
                prefs.couchDbUrl     = couchUrl.trim()
                prefs.couchDbUser    = couchUser.trim()
                prefs.couchDbPassword = couchPass
                prefs.idFuncionario  = idFuncionario.trim()
                ApiClients.initCentral(centralUrl.trim())
                ApiClients.initCouchDb(couchUrl.trim(), couchUser.trim(), couchPass)
                onConfigurado()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Guardar y continuar")
        }
    }
}

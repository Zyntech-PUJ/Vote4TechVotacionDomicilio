package com.vote4tech.domicilio2.ui.config

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vote4tech.domicilio2.util.PrefsManager

@Composable
fun ConfigScreen(prefs: PrefsManager, onConfigGuardada: () -> Unit) {
    var centralApiUrl by remember { mutableStateOf(prefs.centralApiUrl) }
    var couchDbUrl by remember { mutableStateOf(prefs.couchDbUrl) }
    var couchDbUser by remember { mutableStateOf(prefs.couchDbUser) }
    var couchDbPassword by remember { mutableStateOf(prefs.couchDbPassword) }
    var idFuncionario by remember { mutableStateOf(prefs.idFuncionario) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Configuración", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Text("Servidor Central", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = centralApiUrl,
            onValueChange = { centralApiUrl = it },
            label = { Text("URL del servidor central") },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        Text("CouchDB", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = couchDbUrl,
            onValueChange = { couchDbUrl = it },
            label = { Text("URL CouchDB (incluye BD)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = couchDbUser,
            onValueChange = { couchDbUser = it },
            label = { Text("Usuario CouchDB") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = couchDbPassword,
            onValueChange = { couchDbPassword = it },
            label = { Text("Contraseña CouchDB") },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        OutlinedTextField(
            value = idFuncionario,
            onValueChange = { idFuncionario = it },
            label = { Text("ID Funcionario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                prefs.centralApiUrl = centralApiUrl.trim()
                prefs.couchDbUrl = couchDbUrl.trim()
                prefs.couchDbUser = couchDbUser.trim()
                prefs.couchDbPassword = couchDbPassword
                prefs.idFuncionario = idFuncionario.trim()
                prefs.isConfigured = true
                onConfigGuardada()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = centralApiUrl.isNotBlank() && couchDbUrl.isNotBlank() && idFuncionario.isNotBlank()
        ) {
            Text("Guardar configuración")
        }
    }
}

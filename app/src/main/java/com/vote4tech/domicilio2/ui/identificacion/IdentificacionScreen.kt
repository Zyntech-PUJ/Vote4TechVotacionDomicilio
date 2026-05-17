package com.vote4tech.domicilio2.ui.identificacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vote4tech.domicilio2.ui.DomicilioState
import com.vote4tech.domicilio2.ui.DomicilioViewModel
import com.vote4tech.domicilio2.ui.Routes
import kotlinx.coroutines.launch

@Composable
fun IdentificacionScreen(
    viewModel: DomicilioViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    var cedula by remember { mutableStateOf("") }
    var mostrarDialogoUrna by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state) {
        when (val s = state) {
            is DomicilioState.CiudadanoIdentificado -> {
                viewModel.cargarElecciones(s.ciudadano)
            }
            is DomicilioState.EleccionesListas -> {
                navController.navigate(Routes.ELECCION)
            }
            is DomicilioState.CiudadanoUrna -> {
                mostrarDialogoUrna = s.nombre
            }
            else -> {}
        }
    }

    if (mostrarDialogoUrna != null) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoUrna = null
                viewModel.reiniciar()
                cedula = ""
            },
            title = { Text("Acceso Restringido") },
            text = {
                Text("${mostrarDialogoUrna} debe votar en la urna presencial. Este dispositivo es exclusivo para votación domiciliaria.")
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoUrna = null
                    viewModel.reiniciar()
                    cedula = ""
                }) {
                    Text("Entendido")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Identificación", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Votación Domiciliaria", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = cedula,
            onValueChange = { cedula = it.filter { c -> c.isDigit() } },
            label = { Text("Número de cédula") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (state) {
            is DomicilioState.Cargando -> CircularProgressIndicator()
            is DomicilioState.CiudadanoNoEncontrado -> {
                Text(
                    "Ciudadano no encontrado o no habilitado para voto domiciliario",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { viewModel.limpiarError(); cedula = "" }) { Text("Intentar de nuevo") }
            }
            is DomicilioState.YaVoto -> {
                Text("Este ciudadano ya votó en la sesión actual.", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { viewModel.reiniciar(); cedula = "" }) { Text("Nuevo ciudadano") }
            }
            is DomicilioState.Error -> {
                Text((state as DomicilioState.Error).mensaje, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = { viewModel.limpiarError() }) { Text("Volver") }
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.identificarCiudadano(cedula) },
            modifier = Modifier.fillMaxWidth(),
            enabled = cedula.length >= 6 && state !is DomicilioState.Cargando
        ) {
            Text("Verificar ciudadano")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                scope.launch {
                    if (viewModel.tieneDatosLocales()) {
                        navController.navigate(Routes.CONFIG_LOGIN)
                    } else {
                        navController.navigate(Routes.CONFIG)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Configuración")
        }
    }
}

package com.vote4tech.domicilio2.ui.confirmacion

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vote4tech.domicilio2.ui.DomicilioState
import com.vote4tech.domicilio2.ui.DomicilioViewModel
import com.vote4tech.domicilio2.ui.Routes

@Composable
fun ConfirmacionScreen(viewModel: DomicilioViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is DomicilioState.VotoRegistrado) {
            // Queda en pantalla para mostrar el resultado
        }
    }

    when (val s = state) {
        is DomicilioState.CandidatoSeleccionado -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Confirmación de voto", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Elección:", style = MaterialTheme.typography.labelMedium)
                        Text(s.eleccion.nombre, fontWeight = FontWeight.SemiBold)
                        HorizontalDivider()
                        Text("Candidato:", style = MaterialTheme.typography.labelMedium)
                        Text(s.candidato.nombre, fontWeight = FontWeight.SemiBold)
                        Text("Partido: ${s.candidato.partido}", style = MaterialTheme.typography.bodySmall)
                        Text("N°: ${s.candidato.numero}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.confirmarVoto(s.candidato, s.eleccion, s.ciudadano) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirmar voto")
                }

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }

        is DomicilioState.VotoRegistrado -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("✓", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
                Text("Voto registrado localmente", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Cédula: ${s.cedula}", style = MaterialTheme.typography.bodyMedium)
                Text(s.nombreEleccion, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("El voto será enviado al sincronizar.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        viewModel.reiniciar()
                        navController.navigate(Routes.IDENTIFICACION) {
                            popUpTo(Routes.IDENTIFICACION) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Siguiente ciudadano")
                }
            }
        }

        is DomicilioState.Cargando -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Estado inesperado. Reiniciando...")
                LaunchedEffect(Unit) {
                    viewModel.reiniciar()
                    navController.navigate(Routes.IDENTIFICACION) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }
}

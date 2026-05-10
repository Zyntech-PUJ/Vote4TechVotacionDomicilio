package com.vote4tech.domicilio2.ui.votacion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun VotacionScreen(viewModel: DomicilioViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is DomicilioState.CandidatoSeleccionado) {
            navController.navigate(Routes.CONFIRMACION)
        }
    }

    val candidatosState = state as? DomicilioState.CandidatosListos ?: return

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(candidatosState.eleccion.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Seleccione un candidato", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(candidatosState.candidatos) { candidato ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.seleccionarCandidato(candidato, candidatosState.eleccion, candidatosState.ciudadano)
                        }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "${candidato.numero}",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(candidato.nombre, fontWeight = FontWeight.SemiBold)
                            Text(candidato.partido, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

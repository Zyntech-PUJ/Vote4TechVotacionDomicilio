package com.vote4tech.domicilio2.ui.eleccion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vote4tech.domicilio2.ui.DomicilioState
import com.vote4tech.domicilio2.ui.DomicilioViewModel
import com.vote4tech.domicilio2.ui.Routes

@Composable
fun EleccionScreen(viewModel: DomicilioViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is DomicilioState.CandidatosListos) {
            navController.navigate(Routes.VOTACION)
        }
    }

    val eleccionesState = state as? DomicilioState.EleccionesListas ?: return

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Bienvenido/a, ${eleccionesState.ciudadano.nombre}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text("Seleccione la elección", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(eleccionesState.elecciones) { eleccion ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.seleccionarEleccion(eleccion, eleccionesState.ciudadano) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(eleccion.nombre, fontWeight = FontWeight.SemiBold)
                        eleccion.descripcion?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }
    }
}

package com.votacion.domicilio.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.votacion.domicilio.ui.config.ConfigScreen
import com.votacion.domicilio.ui.confirmacion.ConfirmacionScreen
import com.votacion.domicilio.ui.eleccion.EleccionScreen
import com.votacion.domicilio.ui.identificacion.IdentificacionScreen
import com.votacion.domicilio.ui.sync.SyncScreen
import com.votacion.domicilio.ui.votacion.VotacionScreen

object Routes {
    const val CONFIG          = "config"
    const val SYNC            = "sync"
    const val IDENTIFICACION  = "identificacion"
    const val ELECCION        = "eleccion"
    const val VOTACION        = "votacion"
    const val CONFIRMACION    = "confirmacion"
}

@Composable
fun DomicilioNavHost(
    viewModel: DomicilioViewModel,
    startDestination: String
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.CONFIG) {
            ConfigScreen(
                onConfigurado = { navController.navigate(Routes.SYNC) {
                    popUpTo(Routes.CONFIG) { inclusive = true }
                }}
            )
        }

        composable(Routes.SYNC) {
            SyncScreen(
                viewModel = viewModel,
                onIniciarVotacion = { navController.navigate(Routes.IDENTIFICACION) }
            )
        }

        composable(Routes.IDENTIFICACION) {
            IdentificacionScreen(
                viewModel = viewModel,
                onCiudadanoIdentificado = { navController.navigate(Routes.ELECCION) }
            )
        }

        composable(Routes.ELECCION) {
            EleccionScreen(
                viewModel = viewModel,
                onEleccionSeleccionada = { navController.navigate(Routes.VOTACION) }
            )
        }

        composable(Routes.VOTACION) {
            VotacionScreen(
                viewModel = viewModel,
                onCandidatoSeleccionado = { navController.navigate(Routes.CONFIRMACION) }
            )
        }

        composable(Routes.CONFIRMACION) {
            ConfirmacionScreen(
                viewModel = viewModel,
                onVotoGuardado = {
                    navController.navigate(Routes.IDENTIFICACION) {
                        popUpTo(Routes.SYNC) { inclusive = false }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }
    }
}

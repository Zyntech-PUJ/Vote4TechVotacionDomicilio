package com.vote4tech.domicilio2.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vote4tech.domicilio2.ui.config.ConfigScreen
import com.vote4tech.domicilio2.ui.identificacion.IdentificacionScreen
import com.vote4tech.domicilio2.ui.eleccion.EleccionScreen
import com.vote4tech.domicilio2.ui.votacion.VotacionScreen
import com.vote4tech.domicilio2.ui.confirmacion.ConfirmacionScreen
import com.vote4tech.domicilio2.ui.consultas.ConsultasLoginScreen
import com.vote4tech.domicilio2.ui.consultas.ConsultasScreen
import com.vote4tech.domicilio2.util.PrefsManager

@Composable
fun DomicilioNavHost(
    viewModel: DomicilioViewModel,
    prefs: PrefsManager,
    startDestination: String
) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsState()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.CONFIG) {
            ConfigScreen(
                viewModel = viewModel,
                onConsultasClick = {
                    navController.navigate(Routes.CONSULTAS)
                }
            )
        }
        composable(Routes.CONFIG_LOGIN) {
            ConsultasLoginScreen(
                viewModel = viewModel,
                onLoginExito = {
                    navController.navigate(Routes.CONFIG) {
                        popUpTo(Routes.CONFIG_LOGIN) { inclusive = true }
                    }
                },
                onAtras = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.IDENTIFICACION) {
            IdentificacionScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(Routes.ELECCION) {
            EleccionScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(Routes.VOTACION) {
            VotacionScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(Routes.CONFIRMACION) {
            ConfirmacionScreen(
                viewModel = viewModel,
                navController = navController,
                onVotarEnOtraEleccion = {
                    navController.navigate(Routes.ELECCION)
                }
            )
        }
        composable(Routes.CONSULTAS) {
            ConsultasScreen(
                viewModel = viewModel,
                onAtras = {
                    navController.navigate(Routes.IDENTIFICACION) {
                        popUpTo(Routes.CONSULTAS) { inclusive = true }
                    }
                }
            )
        }
    }
}

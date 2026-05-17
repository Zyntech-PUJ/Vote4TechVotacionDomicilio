package com.vote4tech.domicilio2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.vote4tech.domicilio2.ui.DomicilioNavHost
import com.vote4tech.domicilio2.ui.DomicilioViewModel
import com.vote4tech.domicilio2.ui.Routes
import com.vote4tech.domicilio2.ui.theme.Vote4TechVotacionDomicilio2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as DomicilioApplication
        val db = app.database
        val viewModel = ViewModelProvider(
            this,
            DomicilioViewModel.Factory(
                prefs = app.prefs,
                syncRepository = app.syncRepository,
                eleccionDao = db.eleccionDao(),
                candidatoDao = db.candidatoDao(),
                ciudadanoDao = db.ciudadanoLocalDao(),
                votoDao = db.votoLocalDao(),
                funcionarioDao = db.funcionarioDao()
            )
        )[DomicilioViewModel::class.java]

        val startDestination = if (app.prefs.isConfigured) Routes.IDENTIFICACION else Routes.CONFIG

        setContent {
            Vote4TechVotacionDomicilio2Theme {
                DomicilioNavHost(
                    viewModel = viewModel,
                    prefs = app.prefs,
                    startDestination = startDestination
                )
            }
        }
    }
}
package com.votacion.domicilio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.votacion.domicilio.data.repository.SyncRepository
import com.votacion.domicilio.ui.DomicilioNavHost
import com.votacion.domicilio.ui.DomicilioViewModel
import com.votacion.domicilio.ui.Routes
import com.votacion.domicilio.ui.theme.VotacionDomicilioTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: DomicilioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app  = application as DomicilioApplication
        val db   = app.database
        val prefs = app.prefs

        val syncRepo = SyncRepository(
            eleccionDao       = db.eleccionDao(),
            candidatoDao      = db.candidatoDao(),
            ciudadanoLocalDao = db.ciudadanoLocalDao(),
            votoLocalDao      = db.votoLocalDao()
        )

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                DomicilioViewModel(
                    prefs             = prefs,
                    votoDraftDao      = db.votoDraftDao(),
                    votoLocalDao      = db.votoLocalDao(),
                    eleccionDao       = db.eleccionDao(),
                    candidatoDao      = db.candidatoDao(),
                    ciudadanoLocalDao = db.ciudadanoLocalDao(),
                    syncRepo          = syncRepo
                ) as T
        })[DomicilioViewModel::class.java]

        val startDest = if (prefs.isConfigured) Routes.SYNC else Routes.CONFIG

        setContent {
            VotacionDomicilioTheme {
                DomicilioNavHost(
                    viewModel        = viewModel,
                    startDestination = startDest
                )
            }
        }
    }
}

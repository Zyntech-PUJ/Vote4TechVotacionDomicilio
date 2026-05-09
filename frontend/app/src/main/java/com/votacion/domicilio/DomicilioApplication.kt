package com.votacion.domicilio

import android.app.Application
import com.votacion.domicilio.data.local.db.DomicilioDatabase
import com.votacion.domicilio.data.remote.ApiClients
import com.votacion.domicilio.util.PrefsManager

class DomicilioApplication : Application() {

    lateinit var prefs: PrefsManager
        private set

    lateinit var database: DomicilioDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        prefs    = PrefsManager(this)
        database = DomicilioDatabase.getInstance(this)

        // Re-inicializar clientes si ya fue configurado
        if (prefs.isConfigured) {
            ApiClients.initCentral(prefs.centralApiUrl)
            ApiClients.initCouchDb(
                prefs.couchDbUrl,
                prefs.couchDbUser,
                prefs.couchDbPassword
            )
        }
    }
}

package com.vote4tech.domicilio2

import android.app.Application
import com.vote4tech.domicilio2.data.local.db.DomicilioDatabase
import com.vote4tech.domicilio2.data.remote.ApiClients
import com.vote4tech.domicilio2.data.repository.SyncRepository
import com.vote4tech.domicilio2.util.PrefsManager

class DomicilioApplication : Application() {
    lateinit var prefs: PrefsManager
    lateinit var database: DomicilioDatabase
    lateinit var apiClients: ApiClients
    lateinit var syncRepository: SyncRepository

    override fun onCreate() {
        super.onCreate()
        prefs = PrefsManager(this)
        database = DomicilioDatabase.getInstance(this)
        refreshClients()
    }

    fun refreshClients() {
        apiClients = ApiClients(
            centralBaseUrl = prefs.centralApiUrl,
            couchDbBaseUrl = prefs.couchDbUrl,
            couchUser = prefs.couchDbUser,
            couchPassword = prefs.couchDbPassword
        )
        syncRepository = SyncRepository(
            api = apiClients,
            eleccionDao = database.eleccionDao(),
            candidatoDao = database.candidatoDao(),
            ciudadanoDao = database.ciudadanoLocalDao(),
            votoDao = database.votoLocalDao()
        )
    }
}

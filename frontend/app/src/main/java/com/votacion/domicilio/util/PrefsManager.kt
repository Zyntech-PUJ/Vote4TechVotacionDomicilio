package com.votacion.domicilio.util

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("domicilio_config", Context.MODE_PRIVATE)

    /** URL del servidor central (VotacionBack o RegistraduriaBack). */
    var centralApiUrl: String
        get() = prefs.getString(KEY_CENTRAL_URL, "") ?: ""
        set(v) = prefs.edit().putString(KEY_CENTRAL_URL, v).apply()

    /** URL base de CouchDB central (ej: http://central-host:5984). */
    var couchDbUrl: String
        get() = prefs.getString(KEY_COUCH_URL, "") ?: ""
        set(v) = prefs.edit().putString(KEY_COUCH_URL, v).apply()

    var couchDbUser: String
        get() = prefs.getString(KEY_COUCH_USER, "admin") ?: "admin"
        set(v) = prefs.edit().putString(KEY_COUCH_USER, v).apply()

    var couchDbPassword: String
        get() = prefs.getString(KEY_COUCH_PASS, "") ?: ""
        set(v) = prefs.edit().putString(KEY_COUCH_PASS, v).apply()

    /** ID del funcionario responsable de este dispositivo domiciliario. */
    var idFuncionario: String
        get() = prefs.getString(KEY_ID_FUNCIONARIO, "") ?: ""
        set(v) = prefs.edit().putString(KEY_ID_FUNCIONARIO, v).apply()

    val isConfigured: Boolean
        get() = centralApiUrl.isNotBlank() && couchDbUrl.isNotBlank()

    companion object {
        private const val KEY_CENTRAL_URL     = "central_api_url"
        private const val KEY_COUCH_URL       = "couchdb_url"
        private const val KEY_COUCH_USER      = "couchdb_user"
        private const val KEY_COUCH_PASS      = "couchdb_pass"
        private const val KEY_ID_FUNCIONARIO  = "id_funcionario"
    }
}

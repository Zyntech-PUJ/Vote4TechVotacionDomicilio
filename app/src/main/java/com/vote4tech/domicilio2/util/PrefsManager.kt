package com.vote4tech.domicilio2.util

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("domicilio_prefs", Context.MODE_PRIVATE)

    var centralApiUrl: String
        get() = prefs.getString("centralApiUrl", "http://10.0.2.2:8081") ?: "http://10.0.2.2:8081"
        set(value) = prefs.edit().putString("centralApiUrl", value).apply()

    var couchDbUrl: String
        get() = prefs.getString("couchDbUrl", "http://10.0.2.2:5984/votos_domicilio") ?: "http://10.0.2.2:5984/votos_domicilio"
        set(value) = prefs.edit().putString("couchDbUrl", value).apply()

    var couchDbUser: String
        get() = prefs.getString("couchDbUser", "admin") ?: "admin"
        set(value) = prefs.edit().putString("couchDbUser", value).apply()

    var couchDbPassword: String
        get() = prefs.getString("couchDbPassword", "admin123") ?: "admin123"
        set(value) = prefs.edit().putString("couchDbPassword", value).apply()

    var idFuncionario: String
        get() = prefs.getString("idFuncionario", "FUNC-001") ?: "FUNC-001"
        set(value) = prefs.edit().putString("idFuncionario", value).apply()

    var isConfigured: Boolean
        get() = prefs.getBoolean("isConfigured", false)
        set(value) = prefs.edit().putBoolean("isConfigured", value).apply()
}

package com.plataforma.bienestar.data.local

import android.content.Context

// Por ahora no se usa. Útil para guardar datos que quieras conservar de una vista a otra.
class PreferencesManager(context: Context) {
    private val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveUserId(userId: String) {
        sharedPref.edit().putString("user_id", userId).apply()
    }

    fun getUserId(): String? {
        return sharedPref.getString("user_id", null)
    }
}
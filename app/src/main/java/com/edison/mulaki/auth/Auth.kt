package com.edison.mulaki.auth

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

object Auth {
    private const val PREFS_NAME = "AuthPrefs"
    private const val KEY_AUTH = "auth"
    private const val KEY_TOKEN = "token"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var authData : JSONObject
        get() = JSONObject(sharedPreferences.getString(KEY_AUTH, "") ?: "")
        set(value) = sharedPreferences.edit().putString(KEY_AUTH, value.toString()).apply()

    var token: String
        get() = sharedPreferences.getString(KEY_TOKEN, "") ?: ""
        set(value) = sharedPreferences.edit().putString(KEY_TOKEN, value).apply()

    fun isLoggedIn(): Boolean {
        return token.isNotEmpty()
    }

    fun logout() {
        sharedPreferences.edit().remove(KEY_AUTH).remove(KEY_TOKEN).apply()
    }
}
package com.example.shintech

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "ShinTechSession"
    private const val AUTH_TOKEN = "auth_token"
    private const val USER_ROLE = "user_role"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuthToken(context: Context, token: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(AUTH_TOKEN, token)
        editor.apply()
    }

    fun getAuthToken(context: Context): String? {
        return getSharedPreferences(context).getString(AUTH_TOKEN, null)
    }

    fun clearAuthToken(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.remove(AUTH_TOKEN)
        editor.remove(USER_ROLE)
        editor.apply()
    }

    // --- 사용자 역할(Role) 관련 함수 ---
    fun saveUserRole(context: Context, role: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(USER_ROLE, role)
        editor.apply()
    }

    fun getUserRole(context: Context): String {
        return getSharedPreferences(context).getString(USER_ROLE, "GUEST") ?: "GUEST"
    }

    fun isUserAdmin(context: Context): Boolean {
        return getUserRole(context) == "ADMIN"
    }

    fun isGuest(context: Context): Boolean {
        return getUserRole(context) == "GUEST" || getAuthToken(context) == null
    }
}

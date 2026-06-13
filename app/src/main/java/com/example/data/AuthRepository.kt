package com.example.data

import android.content.Context
import android.content.SharedPreferences

class AuthRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("scholar_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_LOGGED_IN_USERNAME = "logged_in_username"
    }

    /**
     * Checks if the student is currently logged in.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Retrieves the username of the currently logged in student.
     */
    fun getLoggedInUsername(): String? {
        return prefs.getString(KEY_LOGGED_IN_USERNAME, null)
    }

    /**
     * Persists the student's logged in state.
     */
    fun login(username: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_LOGGED_IN_USERNAME, username)
            .apply()
    }

    /**
     * Clears the student's logged in state upon logout.
     */
    fun logout() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_LOGGED_IN_USERNAME)
            .apply()
    }

    /**
     * Gets the persisted theme name (falls back to BLACK).
     */
    fun getTheme(): String {
        return prefs.getString("selected_theme", "BLACK") ?: "BLACK"
    }

    /**
     * Persists the selected theme name.
     */
    fun setTheme(themeName: String) {
        prefs.edit().putString("selected_theme", themeName).apply()
    }
}

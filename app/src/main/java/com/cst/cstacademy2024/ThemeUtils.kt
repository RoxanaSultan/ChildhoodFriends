package com.cst.cstacademy2024

import android.content.Context

class ThemeUtils {

    companion object {
        const val PREFS_NAME = "theme_preferences"
        const val THEME_KEY = "theme_key"
        const val LIGHT_MODE = "light"
        const val DARK_MODE = "dark"

        fun saveThemeMode(context: Context, mode: String) {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(THEME_KEY, mode).apply()
        }

        fun loadThemeMode(context: Context): String {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(THEME_KEY, LIGHT_MODE) ?: LIGHT_MODE
        }
    }
}

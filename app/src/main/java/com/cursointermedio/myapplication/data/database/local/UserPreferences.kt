package com.cursointermedio.myapplication.data.database.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cursointermedio.myapplication.domain.model.UserSettings
import com.cursointermedio.myapplication.ui.settings.Language
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.first
import java.util.Locale

class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Define la instancia de DataStore

    companion object {
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val WEIGHT_KG_MODE_KEY = booleanPreferencesKey("weight_kg_mode")
        private val EXERCISE_KG_MODE_KEY = booleanPreferencesKey("exercise_kg_mode")

        private const val LANGUAGE= "language"
        private const val PREFS_NAME = "user_preferences"
        private const val DARK_MODE = ("dark_mode")

        fun applyLanguage(context: Context): Context {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val languageCode = prefs.getString(LANGUAGE, "en") ?: "en"

            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)

            return context.createConfigurationContext(config)
        }

        fun applyDarkMode(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            val isDarkMode = prefs.getBoolean(DARK_MODE, false)
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

    }

    // Lee los datos como un flujo reactivo
    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data
        .map { prefs ->
            val languageCode = prefs[LANGUAGE_KEY] ?: "en"
            UserSettings(
                username = prefs[USERNAME_KEY] ?: "",
                email = prefs[EMAIL_KEY] ?: "",
                isDarkMode = prefs[DARK_MODE_KEY] ?: false,
                isWeightKgMode = prefs[WEIGHT_KG_MODE_KEY] ?: true,
                isExerciseKgMode = prefs[EXERCISE_KG_MODE_KEY] ?: true,
                language = Language.fromCode(languageCode)

            )
        }

    // Guarda los datos
    suspend fun saveUserSettings(settings: UserSettings) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME_KEY] = settings.username
            prefs[EMAIL_KEY] = settings.email
            prefs[DARK_MODE_KEY] = settings.isDarkMode
            prefs[WEIGHT_KG_MODE_KEY] = settings.isWeightKgMode
            prefs[EXERCISE_KG_MODE_KEY] = settings.isExerciseKgMode
            prefs[LANGUAGE_KEY] = settings.language.code
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(LANGUAGE, settings.language.code).apply()
        prefs.edit().putBoolean(DARK_MODE, settings.isDarkMode).apply()

    }
}


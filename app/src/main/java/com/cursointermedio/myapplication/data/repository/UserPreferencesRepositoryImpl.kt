package com.cursointermedio.myapplication.data.repository

import com.cursointermedio.myapplication.data.database.local.UserPreferences
import com.cursointermedio.myapplication.domain.model.UserSettings
import com.cursointermedio.myapplication.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences
) : UserPreferencesRepository {

    // Exponemos un flujo reactivo de UserSettings.
    override val userSettingsFlow: Flow<UserSettings>
        get() = userPreferences.userSettingsFlow

    // Guarda las configuraciones del usuario
    override suspend fun saveUserSettings(settings: UserSettings) {
        userPreferences.saveUserSettings(settings)
    }

}
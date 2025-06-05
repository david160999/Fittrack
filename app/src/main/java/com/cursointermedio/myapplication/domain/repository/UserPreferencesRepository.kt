package com.cursointermedio.myapplication.domain.repository

import com.cursointermedio.myapplication.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userSettingsFlow: Flow<UserSettings>
    suspend fun saveUserSettings(settings: UserSettings)
}
package com.cursointermedio.myapplication.domain.useCase

import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.data.repository.ExerciseRepository
import com.cursointermedio.myapplication.data.repository.UserPreferencesRepositoryImpl
import com.cursointermedio.myapplication.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


class GetUserPreferencesUseCase @Inject constructor(
    private val repository: UserPreferencesRepositoryImpl
) {

    val userSettingsFlow: Flow<UserSettings>
        get() = repository.userSettingsFlow

    suspend fun saveUserSettings(settings: UserSettings) {
        repository.saveUserSettings(settings)
    }

}
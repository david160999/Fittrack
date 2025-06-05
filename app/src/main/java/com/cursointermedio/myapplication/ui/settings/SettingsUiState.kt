package com.cursointermedio.myapplication.ui.settings

import com.cursointermedio.myapplication.domain.model.UserSettings
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel


sealed class SettingsUiState {
    data object Loading : SettingsUiState()
    data class Success(val userSettings: UserSettings) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}
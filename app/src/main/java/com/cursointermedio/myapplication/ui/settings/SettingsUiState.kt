package com.cursointermedio.myapplication.ui.settings

import com.cursointermedio.myapplication.domain.model.UserSettings

sealed class SettingsUiState {
    data object Loading : SettingsUiState()
    data class Success(val userSettings: UserSettings) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}
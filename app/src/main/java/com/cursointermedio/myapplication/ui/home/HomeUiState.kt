package com.cursointermedio.myapplication.ui.home

import com.cursointermedio.myapplication.data.database.entities.DateEntity
import com.cursointermedio.myapplication.domain.model.UserSettings

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val dateInfo: DateEntity, val settings: UserSettings) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
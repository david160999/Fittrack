package com.cursointermedio.myapplication.ui.training

import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts

sealed class TrainingsUiState {
    data object Loading : TrainingsUiState()
    data class Success(val trainings: List<TrainingsWithWeekAndRoutineCounts>) : TrainingsUiState()
    data class Error(val message: String) : TrainingsUiState()
}
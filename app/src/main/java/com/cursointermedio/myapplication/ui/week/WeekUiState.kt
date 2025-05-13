package com.cursointermedio.myapplication.ui.week

import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel

sealed class WeekUiState {
    data object Loading : WeekUiState()
    data class Success(val weeksWithRoutines: List<WeekWithRoutinesModel>) : WeekUiState()
    data class Error(val message: String) : WeekUiState()
}
package com.cursointermedio.myapplication.ui.routine

import com.cursointermedio.myapplication.domain.model.RoutineWithOrderedExercisesModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel

sealed class RoutineUiState {
    data object Loading : RoutineUiState()
    data class Success(val weeksWithRoutines: RoutineWithOrderedExercisesModel) : RoutineUiState()
    data class Error(val message: String) : RoutineUiState()
}
package com.cursointermedio.myapplication.ui.addExercise

import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.RoutineWithOrderedExercisesModel

sealed class AddExerciseUiState {
    data object Loading : AddExerciseUiState()
    data class Success(val exercises: List<ExerciseModel>) : AddExerciseUiState()
    data class Error(val message: String) : AddExerciseUiState()
}
package com.cursointermedio.myapplication.ui.addExercise

import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.RoutineWithOrderedExercisesModel


sealed class CategoryUiState {
    data object Loading : CategoryUiState()
    data class Success(val categoryList: List<CategoryInfo>) : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}
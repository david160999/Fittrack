package com.cursointermedio.myapplication.ui.exercise

import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.domain.model.RoutineWithOrderedExercisesModel

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(val detailList: List<DetailModel>) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}
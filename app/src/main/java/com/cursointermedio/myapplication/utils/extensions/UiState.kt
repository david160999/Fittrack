package com.cursointermedio.myapplication.utils.extensions

sealed class UiState {
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}
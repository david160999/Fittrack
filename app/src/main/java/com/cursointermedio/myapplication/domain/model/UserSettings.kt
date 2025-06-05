package com.cursointermedio.myapplication.domain.model

import com.cursointermedio.myapplication.ui.settings.Language


data class UserSettings(
    val username: String = "",
    val email: String = "",
    val isDarkMode: Boolean = false,
    val isWeightKgMode: Boolean = true,
    val isExerciseKgMode: Boolean = true,
    val language: Language
)
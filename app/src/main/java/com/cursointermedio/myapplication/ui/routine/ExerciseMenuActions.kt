package com.cursointermedio.myapplication.ui.routine

import com.cursointermedio.myapplication.domain.model.ExerciseModel

data class ExerciseMenuActions(
    val onDescription: (ExerciseModel) -> Unit,
    val onEliminate: (ExerciseModel) -> Unit
)


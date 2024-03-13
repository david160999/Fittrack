package com.cursointermedio.myapplication.domain.model

import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity

data class ExerciseModel(
    val id: Int,
    val name: String?,
    val description: String?,
)
fun ExerciseEntity.toDomain() = ExerciseModel(exerciseId, name, description)


data class RealSeriesModel(
    val id: Int?,
    val weight: Int?,
    val reps: Int?,
    val rpe: Int?,
    val objective: Int?
)

data class ObjectiveSeriesModel(
    val id: Int?,
    val weight: Int?,
    val reps: Int?,
    val rpe: Int?,
    val objective: Int?
)
package com.cursointermedio.myapplication.domain.model

import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity

data class ExerciseModel(
    val id: Long?,
    val categoryExerciseId:Long,
    val name: String,
    val description: String?,
)
fun ExerciseEntity.toDomain() = ExerciseModel(exerciseId, categoryExerciseId, name, description)


data class RealSeriesModel(
    val id: Long?,
    val weight: Int?,
    val reps: Int?,
    val rpe: Int?,
    val objective: Int?
)

data class ObjectiveSeriesModel(
    val id: Long?,
    val weight: Int?,
    val reps: Int?,
    val rpe: Int?,
    val objective: Int?
)
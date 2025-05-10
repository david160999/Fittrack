package com.cursointermedio.myapplication.domain.model

import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeks

data class TrainingFirebaseModel(
    val trainingId: Long? = null,
    val name: String = "",
    val description: String? = null,
    val weeks: List<WeekFirebaseModel> = emptyList()
)

data class WeekFirebaseModel(
    val weekId: Long? = null,
    val trainingWeekId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val routines: List<RoutineFirebaseModel> = emptyList()
)

data class RoutineFirebaseModel(
    val routineId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val exercises: List<ExerciseFirebaseModel> = emptyList()
)

data class ExerciseFirebaseModel(
    val exerciseId: Long? = null,
    val key: String? = null,
    val name: String? = null,
    val categoryExerciseId: Long? = null,
    val details: List<DetailFirebaseModel> = emptyList()
)

data class DetailFirebaseModel(
    val detailsId: Long? = null,
    val objWeight: Int? = null,
    val objReps: Int? = null,
    val objRpe: Int? = null,
    val realWeight: Int? = null,
    val realReps: Int? = null,
    val realRpe: Int? = null
)


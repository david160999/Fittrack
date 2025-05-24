package com.cursointermedio.myapplication.domain.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.WeekEntity

data class RoutineModel(
    val routineId: Long?,
    val weekRoutineId: Long,
    var name: String?,
    val description: String?,
    var order: Int?,
    val exerciseCount: Int? = 0,
    val date: String? = null,
)

fun RoutineEntity.toDomain() = RoutineModel(routineId, weekRoutineId, name, description, order)


data class RoutineWithOrderedExercisesModel(
    val routine: RoutineModel,
    val exercises: List<ExerciseModel>
)
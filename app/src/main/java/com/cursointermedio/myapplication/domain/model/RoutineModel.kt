package com.cursointermedio.myapplication.domain.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.WeekEntity

data class RoutineModel(
    val routineId: Long?,
    val weekRoutineId: Long,
    var name: String?,
    val description: String?,
    var exerciseCount: Int? = 0,
    var date: String? = ""
)

fun RoutineEntity.toDomain() = RoutineModel(routineId, weekRoutineId, name, description)

package com.cursointermedio.myapplication.domain.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class RoutineModel(
    val routineId: Long?,
    val weekRoutineId: Long,
    val name: String?,
    val description: String?
)


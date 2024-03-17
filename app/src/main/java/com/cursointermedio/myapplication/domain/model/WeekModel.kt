package com.cursointermedio.myapplication.domain.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.WeekEntity

data class WeekModel
    (
    val weekId: Int,
    val trainingWeekId: Int,
    val name: String?,
    val description: String?
)

fun WeekEntity.toDomain() = WeekModel(weekId, trainingWeekId, name, description)


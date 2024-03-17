package com.cursointermedio.myapplication.domain.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.WeekEntity

data class TrainingModel
    (
    val trainingId: Int?,
    val name: String,
    val description: String?
)

fun TrainingEntity.toDomain() = TrainingModel(trainingId, name,description)


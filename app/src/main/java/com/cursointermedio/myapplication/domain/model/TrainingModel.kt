package com.cursointermedio.myapplication.domain.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import kotlinx.coroutines.flow.Flow


data class TrainingModel
    (
    val trainingId: Long?,
    var name: String,
    val description: String?,
)

fun TrainingEntity.toDomain() = TrainingModel(trainingId, name,description)


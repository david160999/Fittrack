package com.cursointermedio.myapplication.domain.model

import com.cursointermedio.myapplication.data.database.entities.WeekEntity

data class WeekModel
    (
    val weekId: Int?,
    val trainingWeekId: Int,
    val name: String?,
    val description: String?
)

fun WeekEntity.toDomain() = WeekModel(weekId, trainingWeekId, name, description)


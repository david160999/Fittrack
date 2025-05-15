package com.cursointermedio.myapplication.domain.model

import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines

data class WeekWithRoutinesModel(
    val week: WeekModel,
    val routineList: List<RoutineModel>
)


fun WeekWithRoutines.toDomain() = WeekWithRoutinesModel(week.toDomain(), routineList.map { it.toDomain() })

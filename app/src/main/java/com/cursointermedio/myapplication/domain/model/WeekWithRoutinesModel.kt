package com.cursointermedio.myapplication.domain.model

import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines

data class WeekWithRoutinesModel(
    val week: WeekEntity,
    val routineList: List<RoutineEntity>
)


fun WeekWithRoutines.toDomain() = WeekWithRoutinesModel(week, routineList)

package com.cursointermedio.myapplication.data.repository

import com.cursointermedio.myapplication.data.database.dao.RoutineDao
import com.cursointermedio.myapplication.data.database.dao.WeekDao
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineWithExercises
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import javax.inject.Inject

class RoutineRepository @Inject constructor(
    private val routineDao: RoutineDao
) {
    suspend fun insertRoutineToWeek(routine: RoutineEntity): Long {
        return routineDao.insertRoutineToWeek(routine)
    }

    suspend fun getRoutineWithExercises(routineId: Long): RoutineWithExercises {
        return routineDao.getRoutineWithExercises(routineId)
    }
}
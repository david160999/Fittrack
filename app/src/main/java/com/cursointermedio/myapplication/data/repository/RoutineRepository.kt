package com.cursointermedio.myapplication.data.repository

import com.cursointermedio.myapplication.data.database.dao.RoutineDao
import com.cursointermedio.myapplication.data.database.dao.WeekDao
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineWithExercises
import com.cursointermedio.myapplication.data.database.entities.RoutineWithOrderedExercises
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.domain.model.RoutineModel
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

    suspend fun changeNameRoutine(routine: RoutineEntity) {
        routineDao.changeNameRoutine(routine)
    }

    suspend fun deleteRoutine(routine: RoutineEntity){
        routineDao.deleteRoutine(routine)
    }

    suspend fun changeOrderRoutines(routines: List<RoutineEntity>) {
        routineDao.changeOrderRoutines(routines)
    }

    suspend fun getRoutineWithOrderedExercises(routineId: Long): RoutineWithOrderedExercises {
       return routineDao.getRoutineWithOrderedExercises(routineId)
    }


}
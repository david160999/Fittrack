package com.cursointermedio.myapplication.data.repository

import androidx.room.Query
import com.cursointermedio.myapplication.data.database.dao.RoutineDao
import com.cursointermedio.myapplication.data.database.dao.WeekDao
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.RoutineWithExercises
import com.cursointermedio.myapplication.data.database.entities.RoutineWithOrderedExercises
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.RoutineWithOrderedExercisesModel
import com.cursointermedio.myapplication.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoutineRepository @Inject constructor(
    private val routineDao: RoutineDao
) {
    suspend fun insertRoutineToWeek(routine: RoutineEntity): Long {
        return routineDao.insertRoutineToWeek(routine)
    }

    suspend fun getRoutineById(routineId: Long): RoutineModel{
        return routineDao.getRoutineById(routineId).toDomain()
    }

    suspend fun getRoutineWithExercises(routineId: Long): RoutineWithExercises {
        return routineDao.getRoutineWithExercises(routineId)
    }

    suspend fun changeNameRoutine(routine: RoutineEntity) {
        routineDao.changeNameRoutine(routine)
    }

    suspend fun deleteRoutine(routine: RoutineEntity) {
        routineDao.deleteRoutine(routine)
    }

    suspend fun changeOrderRoutines(routines: List<RoutineEntity>) {
        routineDao.changeOrderRoutines(routines)
    }

    suspend fun getRoutineWithOrderedExercises(routineId: Long): RoutineWithOrderedExercisesModel {
        val respond = routineDao.getRoutineWithOrderedExercises(routineId)

        return RoutineWithOrderedExercisesModel(
            routine = respond.routine.toDomain(),
            exercises = respond.exercises.map { it.toDomain() }
        )
    }

    fun getRoutineWithOrderedExercisesFlow(routineId: Long): Flow<RoutineWithOrderedExercisesModel> {
        return routineDao.getRoutineWithOrderedExercisesFlow(routineId).map { entity ->
            RoutineWithOrderedExercisesModel(
                routine = entity.routine.toDomain(),
                exercises = entity.exercises.map { it.toDomain() }
            )
        }
    }

    suspend fun removeExerciseFromRoutine(crossRef: RoutineExerciseCrossRef) {
        routineDao.removeExerciseFromRoutine(crossRef)
    }

    suspend fun updateOrderCrossRefRoutineExercise(exerciseId: Long, routineId: Long, order:Int){
        routineDao.updateOrderCrossRefRoutineExercise(exerciseId = exerciseId, routineId = routineId, order = order)
    }

    }
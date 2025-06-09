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
    // Inserta una rutina en una semana específica
    suspend fun insertRoutineToWeek(routine: RoutineEntity): Long {
        return routineDao.insertRoutineToWeek(routine)
    }

    // Obtiene una rutina por ID y la convierte al modelo de dominio
    suspend fun getRoutineById(routineId: Long): RoutineModel {
        return routineDao.getRoutineById(routineId).toDomain()
    }

    // Cambia el nombre de una rutina
    suspend fun changeNameRoutine(routine: RoutineEntity) {
        routineDao.changeNameRoutine(routine)
    }

    // Elimina la rutina completa
    suspend fun deleteRoutine(routine: RoutineEntity) {
        routineDao.deleteRoutine(routine)
    }

    // Cambia el orden de todas las rutinas (probablemente dentro de una semana)
    suspend fun changeOrderRoutines(routines: List<RoutineEntity>) {
        routineDao.changeOrderRoutines(routines)
    }

    // Rutina + ejercicios ordenados (suspend)
    suspend fun getRoutineWithOrderedExercises(routineId: Long): RoutineWithOrderedExercisesModel {
        val result = routineDao.getRoutineWithOrderedExercises(routineId)
        return RoutineWithOrderedExercisesModel(
            routine = result.routine.toDomain(),
            exercises = result.exercises.map { it.toDomain() }
        )
    }

    // Rutina + ejercicios ordenados (flow reactivo)
    fun getRoutineWithOrderedExercisesFlow(routineId: Long): Flow<RoutineWithOrderedExercisesModel> {
        return routineDao.getRoutineWithOrderedExercisesFlow(routineId)
            .map { entity ->
                RoutineWithOrderedExercisesModel(
                    routine = entity.routine.toDomain(),
                    exercises = entity.exercises.map { it.toDomain() }
                )
            }
    }

    // Elimina una relación rutina-ejercicio
    suspend fun removeExerciseFromRoutine(crossRef: RoutineExerciseCrossRef) {
        routineDao.removeExerciseFromRoutine(crossRef)
    }

    // Actualiza el orden de un ejercicio dentro de una rutina
    suspend fun updateOrderCrossRefRoutineExercise(
        exerciseId: Long,
        routineId: Long,
        order: Int
    ) {
        routineDao.updateOrderCrossRefRoutineExercise(
            exerciseId = exerciseId,
            routineId = routineId,
            order = order
        )
    }
}
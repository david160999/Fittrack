package com.cursointermedio.myapplication.domain.useCase

import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineWithExercises
import com.cursointermedio.myapplication.data.database.entities.RoutineWithOrderedExercises
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.data.repository.ExerciseRepository
import com.cursointermedio.myapplication.data.repository.RoutineRepository
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRoutineUseCase @Inject constructor(
    private val repository: RoutineRepository
) {
    suspend fun insertRoutineToWeek(routine: RoutineModel): Long {
        return repository.insertRoutineToWeek(routine.toDatabase())
    }

    suspend fun getRoutineWithExercises(routineId: Long): RoutineWithExercises {
        return repository.getRoutineWithExercises(routineId)
    }

    suspend fun copyRoutine(rutinaOriginal: RoutineModel, nuevoWeekId: Long): Long {
        val nuevaRutina = RoutineModel(
            routineId = null,
            weekRoutineId = nuevoWeekId,
            name = rutinaOriginal.name,
            description = rutinaOriginal.description,
            order = rutinaOriginal.order
        )
        return insertRoutineToWeek(nuevaRutina)
    }

    suspend fun changeNameRoutine(routine: RoutineModel) {
        repository.changeNameRoutine(routine.toDatabase())
    }

    suspend fun deleteRoutine(routine: RoutineModel) {
        repository.deleteRoutine(routine.toDatabase())
    }

    suspend fun changeOrderRoutines(routines: List<RoutineModel>) {
        repository.changeOrderRoutines(routines.map { it.toDatabase() })
    }

    suspend fun getRoutineWithOrderedExercises(routineId: Long): RoutineWithOrderedExercises {
        return repository.getRoutineWithOrderedExercises(routineId)
    }
}
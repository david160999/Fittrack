package com.cursointermedio.myapplication.domain.useCase

import androidx.room.Index
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.RoutineWithExercises
import com.cursointermedio.myapplication.data.database.entities.RoutineWithOrderedExercises
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.data.repository.ExerciseRepository
import com.cursointermedio.myapplication.data.repository.RoutineRepository
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.RoutineWithOrderedExercisesModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRoutineUseCase @Inject constructor(
    private val repository: RoutineRepository
) {
    // Inserta una rutina en una semana y devuelve el ID generado.
    suspend fun insertRoutineToWeek(routine: RoutineModel): Long {
        return repository.insertRoutineToWeek(routine.toDatabase())
    }

    // Obtiene una rutina específica por su ID.
    suspend fun getRoutineById(routineId: Long): RoutineModel {
        return repository.getRoutineById(routineId)
    }

    // Copia una rutina existente a una nueva semana, preservando nombre, descripción y orden.
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

    // Cambia el nombre de una rutina existente.
    suspend fun changeNameRoutine(routine: RoutineModel) {
        repository.changeNameRoutine(routine.toDatabase())
    }

    // Elimina una rutina de la base de datos.
    suspend fun deleteRoutine(routine: RoutineModel) {
        repository.deleteRoutine(routine.toDatabase())
    }

    // Cambia el orden de una lista de rutinas.
    suspend fun changeOrderRoutines(routines: List<RoutineModel>) {
        repository.changeOrderRoutines(routines.map { it.toDatabase() })
    }

    // Obtiene una rutina con sus ejercicios ordenados según el orden definido.
    suspend fun getRoutineWithOrderedExercises(routineId: Long): RoutineWithOrderedExercisesModel {
        return repository.getRoutineWithOrderedExercises(routineId)
    }

    // Elimina la relación entre un ejercicio y una rutina.
    suspend fun removeExerciseFromRoutine(crossRef: RoutineExerciseCrossRef) {
        repository.removeExerciseFromRoutine(crossRef)
    }

    // Devuelve un Flow que emite la rutina con ejercicios ordenados de manera reactiva.
    fun getRoutineWithOrderedExercisesFlow(routineId: Long): Flow<RoutineWithOrderedExercisesModel> {
        return repository.getRoutineWithOrderedExercisesFlow(routineId)
    }

    // Actualiza el orden de un ejercicio dentro de una rutina.
    suspend fun updateOrderCrossRefRoutineExercise(exerciseId: Long, routineId: Long, order: Int) {
        repository.updateOrderCrossRefRoutineExercise(exerciseId = exerciseId, routineId = routineId, order = order)
    }
}
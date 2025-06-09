package com.cursointermedio.myapplication.domain.useCase

import com.cursointermedio.myapplication.data.database.entities.ExerciseDetailsCount
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.data.repository.ExerciseRepository
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository,
    private val getRoutineUseCase: GetRoutineUseCase
) {
    // Obtiene un Flow con la lista completa de ejercicios desde la base de datos.
    fun getAllExercisesFromDatabase(): Flow<List<ExerciseModel>> {
        return repository.getAllExercisesFromDatabase()
    }

    // Inserta una relación entre un ejercicio y una rutina en la base de datos.
    suspend fun insertExerciseToRoutine(exercise: RoutineExerciseCrossRef) =
        repository.insertExerciseToRoutine(exercise)

    // Obtiene una lista de ejercicios que pertenecen a una categoría específica.
    suspend fun getExercisesFromCategory(categoryId: Long): List<ExerciseModel> {
        return repository.getExercisesFromCategory(categoryId)
    }

    // Obtiene la lista de categorías disponibles.
    suspend fun getCategories(): List<CategoryInfo> = repository.getCategories()

    // Inserta un ejercicio en la base de datos transformando el modelo de dominio a entidad.
    suspend fun insertExercise(exercise: ExerciseModel) {
        repository.insertExercise(exercise.toDatabase())
    }

    // Copia los ejercicios de una rutina a otra nueva, manteniendo el orden original y sin notas.
    suspend fun copyExercise(routineId: Long, newRoutineId: Long) {
        val exercises = getRoutineUseCase.getRoutineWithOrderedExercises(routineId)
        exercises.exercises.forEachIndexed { index, exercise ->
            val relation = RoutineExerciseCrossRef(
                routineId = newRoutineId,
                exerciseId = exercise.id!!,
                order = index,
                notes = null
            )
            insertExerciseToRoutine(relation)
        }
    }

    // Obtiene un Flow con el conteo de ejercicios asociados a una rutina.
    fun getExerciseFromRoutineCountFlow(routineId: Long): Flow<Int> {
        return repository.getExerciseFromRoutineCountFlow(routineId)
    }

    // Obtiene el conteo de detalles para un ejercicio específico dentro de una rutina.
    suspend fun getDetailCountFromExercise(exerciseId: Long, routineId: Long): Int {
        return repository.getDetailCountFromExercise(exerciseId, routineId)
    }

    // Obtiene el número total de ejercicios asociados a una rutina.
    suspend fun getExerciseFromRoutineCount(routineId: Long): Int {
        return repository.getExerciseFromRoutineCount(routineId)
    }

    // Obtiene las notas asociadas a una relación específica entre rutina y ejercicio.
    suspend fun getNotesFromCrossRef(routineId: Long, exerciseId: Long): String? {
        return repository.getNotesFromCrossRef(routineId = routineId, exerciseId = exerciseId)
    }

    // Actualiza las notas para la relación entre rutina y ejercicio.
    suspend fun updateNotesFromCrossRef(routineId: Long, exerciseId: Long, notes: String) {
        repository.updateNotesFromCrossRef(routineId = routineId, exerciseId = exerciseId, notes)
    }

    // Obtiene el modelo completo de un ejercicio específico.
    suspend fun getExercise(exerciseId: Long): ExerciseModel {
        return repository.getExercise(exerciseId)
    }
}
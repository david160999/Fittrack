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
    fun getAllExercisesFromDatabase(): Flow<List<ExerciseModel>> {
        return repository.getAllExercisesFromDatabase()
    }

    suspend fun insertExerciseToRoutine(exercise: RoutineExerciseCrossRef) =
        repository.insertExerciseToRoutine(exercise)

    suspend fun getExercisesFromCategory(categoryId: Long): List<ExerciseModel> {
        return repository.getExercisesFromCategory(categoryId)
    }

    suspend fun getCategories(): List<CategoryInfo> = repository.getCategories()

    suspend fun insertExercise(exercise: ExerciseModel) {
        repository.insertExercise(exercise.toDatabase())
    }

    suspend fun getExerciseDetailsCount(routineId: Long): List<ExerciseDetailsCount> {
        return repository.getExerciseDetailsCount(routineId)
    }

    suspend fun copyExercise(routineId: Long, newRoutineId: Long) {
        val exercises = getRoutineUseCase.getRoutineWithOrderedExercises(routineId)
        exercises.exercises.forEachIndexed { index, exercise ->
            val relation = RoutineExerciseCrossRef(
                routineId = newRoutineId,  // ID de la rutina
                exerciseId = exercise.id!!,  // ID del ejercici
                order = index,
                notes = null
            )
            insertExerciseToRoutine(relation)
        }
    }

    fun getExerciseFromRoutineCountFlow(routineId: Long): Flow<Int> {
        return repository.getExerciseFromRoutineCountFlow(routineId)
    }

    suspend fun getDetailCountFromExercise(exerciseId: Long, routineId: Long):Int {
        return repository.getDetailCountFromExercise(exerciseId, routineId)

    }

    suspend fun getExerciseFromRoutineCount(routineId: Long): Int {
        return repository.getExerciseFromRoutineCount(routineId)
    }

    fun changeOrderRoutines(newCrossRefList: List<RoutineExerciseCrossRef>) {

    }

    suspend fun getNotesFromCrossRef(routineId: Long, exerciseId: Long): String?{
        return repository.getNotesFromCrossRef(routineId = routineId, exerciseId = exerciseId)
    }

    suspend fun updateNotesFromCrossRef(routineId: Long, exerciseId: Long, notes:String){
        repository.updateNotesFromCrossRef(routineId = routineId, exerciseId = exerciseId, notes)
    }

    suspend fun getExercise(exerciseId: Long): ExerciseModel{
        return repository.getExercise(exerciseId)
    }
}
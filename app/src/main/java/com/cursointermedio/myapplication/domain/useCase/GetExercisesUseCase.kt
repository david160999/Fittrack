package com.cursointermedio.myapplication.domain.useCase

import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.data.repository.ExerciseRepository
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    fun invoke(): Flow<List<ExerciseModel>> {
        return repository.getAllExercisesFromDatabase()
    }

    suspend fun insertExerciseToRoutine(exercise: RoutineExerciseCrossRef) = repository.insertExerciseToRoutine(exercise)

    suspend fun getExercisesFromCategory(categoryId: Long): List<ExerciseModel> {
        return repository.getExercisesFromCategory(categoryId)
    }

    suspend fun getCategories(): List<CategoryInfo> = repository.getCategories()

    suspend fun insertExercise(exercise: ExerciseModel) {
        repository.insertExercise(exercise.toDatabase())
    }

}
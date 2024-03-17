package com.cursointermedio.myapplication.domain.useCase

import com.cursointermedio.myapplication.data.repository.ExerciseRepository
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    suspend operator fun invoke(): Flow<MutableList<ExerciseModel>> {
        return repository.getAllExercisesFromDatabase()
    }
}
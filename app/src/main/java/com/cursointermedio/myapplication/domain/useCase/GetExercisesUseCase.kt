package com.cursointermedio.myapplication.domain.useCase

import com.cursointermedio.myapplication.data.repository.ExerciseRepository
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    suspend operator fun invoke(): List<ExerciseModel> {
        return repository.getAllExercisesFromDatabase()
    }
}
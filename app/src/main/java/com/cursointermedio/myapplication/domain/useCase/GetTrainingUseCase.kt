package com.cursointermedio.myapplication.domain.useCase

import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.data.repository.ExerciseRepository
import com.cursointermedio.myapplication.data.repository.TrainingRepository
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.TrainingModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrainingUseCase @Inject constructor(
    private val repository: TrainingRepository
) {
    operator fun invoke(): Flow<List<TrainingModel>> {
        return repository.getAllTrainingsFromDatabase()
    }

    suspend fun insertTraining(training :TrainingModel): Long {
        return repository.insertTraining(training.toDatabase())
    }

    fun getTrainingWithWeeksAndRoutines() : Flow<List<TrainingWithWeeksAndRoutines>> = repository.getTrainingWithWeeksAndRoutines()


    suspend fun deleteAll(){
        return repository.deleteAll()
    }
}
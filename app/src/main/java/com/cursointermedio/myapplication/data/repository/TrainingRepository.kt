package com.cursointermedio.myapplication.data.repository

import com.cursointermedio.myapplication.data.database.dao.ExerciseDao
import com.cursointermedio.myapplication.data.database.dao.TrainingDao
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrainingRepository @Inject constructor(
    private val trainingDao: TrainingDao
) {
    fun getAllTrainingsFromDatabase(): Flow<List<TrainingModel>> {
        val response = trainingDao.getAllTraining()
        return response.map { it -> it.map { it.toDomain()} }
    }

    suspend fun insertTraining(training: TrainingEntity) {
        trainingDao.insertTraining(training)
    }

    suspend fun deleteTraining(training: TrainingEntity) {
        trainingDao.deleteTraining(training)
    }


    fun getTrainingWithWeeksAndRoutines(): Flow<List<TrainingWithWeeksAndRoutines>> = trainingDao.getTrainingWithWeeksAndRoutines()


    suspend fun deleteAll(){
        return trainingDao.deleteAllTraining()
    }

}
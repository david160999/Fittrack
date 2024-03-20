package com.cursointermedio.myapplication.data.repository

import com.cursointermedio.myapplication.data.database.dao.TrainingDao
import com.cursointermedio.myapplication.data.database.dao.WeekDao
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeekRepository @Inject constructor(
    private val weekDao: WeekDao
) {
//    fun getWeeksTrainingFromDatabase(trainingID:Int): Flow<MutableList<WeekEntity>> {
//        val response = weekDao.getWeeksTraining(trainingID)
//        return response.map { it -> it.map { it.toDomain()} }
//    }
//
//    suspend fun insertTraining(training: TrainingEntity) {
//        trainingDao.insertTraining(training)
//    }
//
//    suspend fun deleteTraining(training: TrainingEntity) {
//        trainingDao.deleteTraining(training)
//    }
//
//
//    fun getTrainingWithWeeksAndRoutines(): Flow<List<TrainingWithWeeksAndRoutines>> = trainingDao.getTrainingWithWeeksAndRoutines()
//
//
//    suspend fun deleteAll(){
//        return trainingDao.deleteAllTraining()
//    }

}
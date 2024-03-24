package com.cursointermedio.myapplication.data.repository

import com.cursointermedio.myapplication.data.database.dao.TrainingDao
import com.cursointermedio.myapplication.data.database.dao.WeekDao
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeekRepository @Inject constructor(
    private val weekDao: WeekDao
) {
    fun getWeeksTrainingFromDatabase(trainingID:Int): Flow<List<WeekModel>> {
        val response = weekDao.getWeeksTraining(trainingID)
        return response.map { it -> it.map { it.toDomain()} }
    }

    suspend fun insertWeekToTraining(week: WeekEntity) {
        weekDao.insertWeekToTraining(week)
    }

    suspend fun deleteWeek(week: WeekModel) {
        weekDao.deleteWeek(week.toDatabase())
    }
    fun getWeeksWithRoutines(trainingID: Int): Flow<List<WeekWithRoutines>> = weekDao.getWeeksWithRoutines(trainingID)


//    suspend fun deleteAll(){
//        return trainingDao.deleteAllTraining()
//    }

}
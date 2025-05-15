package com.cursointermedio.myapplication.data.repository

import com.cursointermedio.myapplication.data.database.dao.WeekDao
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeekRepository @Inject constructor(
    private val weekDao: WeekDao
) {
    fun getWeeksTrainingFromDatabase(trainingID:Long): Flow<List<WeekModel>> {
        val response = weekDao.getWeeksTraining(trainingID)
        return response.map { it -> it.map { it.toDomain()} }
    }

    suspend fun insertWeekToTraining(week: WeekEntity): Long {
        return weekDao.insertWeekToTraining(week)
    }

    suspend fun deleteWeek(week: WeekModel) {
        weekDao.deleteWeek(week.toDatabase())
    }
     fun getAllWeeksWithRoutines(trainingID: Long): Flow<List<WeekWithRoutinesModel>> {
         val response = weekDao.getAllWeeksWithRoutines(trainingID)
         return response.map { it -> it.map { it.toDomain()} }
     }

    suspend fun getWeekWithRoutines(weekId:Long): WeekWithRoutinesModel{
        val response = weekDao.getWeekWithRoutines(weekId)
        return response.toDomain()
    }

    suspend fun getTrainingName(trainingId: Long): String {
        return weekDao.getTrainingName(trainingId)
    }

//    suspend fun deleteAll(){
//        return trainingDao.deleteAllTraining()
//    }

}
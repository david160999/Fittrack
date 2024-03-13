package com.cursointermedio.myapplication.data.repository

import com.cursointermedio.myapplication.data.database.dao.ExerciseDao
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.toDomain
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {

    suspend fun getAllExercisesFromDatabase(): List<ExerciseModel>{
        val response = exerciseDao.getAllExercises()
        return response.map { it.toDomain() }
    }

    suspend fun insertExercises(exercises:List<ExerciseEntity>){
        exerciseDao.insertAll(exercises)
    }

    suspend fun clearExercises(){
        exerciseDao.deleteAllExercises()
    }
}
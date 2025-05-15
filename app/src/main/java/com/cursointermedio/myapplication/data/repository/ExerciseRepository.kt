package com.cursointermedio.myapplication.data.repository

import android.util.Log
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.cursointermedio.myapplication.data.database.dao.ExerciseDao
import com.cursointermedio.myapplication.data.database.entities.CategoryEntity
import com.cursointermedio.myapplication.data.database.entities.DetailsEntity
import com.cursointermedio.myapplication.data.database.entities.ExerciseDetailsCount
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {

    fun getAllExercisesFromDatabase(): Flow<List<ExerciseModel>> {
        val response = exerciseDao.getAllExercises()
        return response.map { it -> it.map { it.toDomain() } }
    }

    suspend fun insertExercise(exercises: ExerciseEntity) {
        exerciseDao.insertExercise(exercises)
    }

    suspend fun insertExerciseToRoutine(exercise: RoutineExerciseCrossRef) =
        exerciseDao.insertExerciseToRoutine(exercise)

    suspend fun getExercisesFromCategory(categoryId: Long): List<ExerciseModel> {
        val response = exerciseDao.getExercisesFromCategory(categoryId)
        return response.map { it.toDomain() }
    }


    suspend fun getCategories(): List<CategoryInfo> {
        val response = exerciseDao.getCategories()
        Log.d("DAO_TEST", "Categories from DAO: $response")

        return response.map {
            it.toDomain()
        }
    }

    suspend fun getExerciseDetailsCount(routineId: Long): List<ExerciseDetailsCount> {
        return exerciseDao.getExerciseDetailsCount(routineId)
    }

    suspend fun getExerciseFromRoutineCount(routineId: Long): Int {
        return exerciseDao.getExerciseFromRoutineCount(routineId)
    }


}
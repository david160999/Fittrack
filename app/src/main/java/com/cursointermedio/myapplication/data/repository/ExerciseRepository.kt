package com.cursointermedio.myapplication.data.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.cursointermedio.myapplication.data.database.dao.ExerciseDao
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {

    suspend fun getAllExercisesFromDatabase(): Flow<MutableList<ExerciseModel>> {
        val response = exerciseDao.getAllExercises()
        return response.map { it -> it.map { it.toDomain() }.toMutableList() }
    }

    suspend fun insertExercise(exercises: ExerciseEntity) {
        exerciseDao.insertExercise(exercises)
    }
    suspend fun insertExerciseToRoutine(exercise: RoutineExerciseCrossRef) = exerciseDao.insertExerciseToRoutine(exercise)

}
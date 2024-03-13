package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercise_table")
    suspend fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercise_table WHERE :routineID = exerciseId")
    suspend fun getExercisesRoutine(routineID: Int): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseToRoutine(exercise: RoutineExerciseCrossRef)

}
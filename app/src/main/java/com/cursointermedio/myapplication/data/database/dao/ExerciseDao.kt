package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cursointermedio.myapplication.data.database.entities.CategoryEntity
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import kotlinx.coroutines.flow.Flow
import java.util.Locale.Category

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercise_table")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercise_table WHERE :routineID = exerciseId")
    fun getExercisesRoutine(routineID: Int): Flow<MutableList<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseToRoutine(exercise: RoutineExerciseCrossRef)

    @Query("SELECT * FROM exercise_table WHERE :categoryId = categoryExerciseId")
    suspend fun getExercisesFromCategory(categoryId: Long): List<ExerciseEntity>


    @Query("SELECT * FROM category_table")
    suspend fun getCategories(): List<CategoryEntity>

}
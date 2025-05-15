package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cursointermedio.myapplication.data.database.entities.CategoryEntity
import com.cursointermedio.myapplication.data.database.entities.ExerciseDetailsCount
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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseToRoutine(exercise: RoutineExerciseCrossRef)

    @Query("SELECT * FROM exercise_table WHERE :categoryId = categoryExerciseId")
    suspend fun getExercisesFromCategory(categoryId: Long): List<ExerciseEntity>


    @Query("SELECT * FROM category_table")
    suspend fun getCategories(): List<CategoryEntity>

    @Transaction
    @Query("""
        SELECT e.exerciseId, COUNT(d.detailsId) as detailsCount
        FROM exercise_table e
        LEFT JOIN details_table d ON e.exerciseId = d.exerciseDetailsId
        WHERE e.exerciseId IN (SELECT exerciseId FROM RoutineExerciseCrossRef WHERE routineId = :routineId)
        GROUP BY e.exerciseId
        ORDER BY e.exerciseId
    """)
    suspend fun getExerciseDetailsCount(routineId: Long): List<ExerciseDetailsCount>

    @Query("SELECT COUNT(*)  FROM RoutineExerciseCrossRef WHERE routineId = :routineId")
    suspend fun getExerciseFromRoutineCount(routineId: Long): Int
}
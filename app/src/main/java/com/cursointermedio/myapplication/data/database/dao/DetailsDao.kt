package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cursointermedio.myapplication.data.database.entities.DetailsEntity
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface DetailsDao {

    @Query("SELECT * FROM details_table WHERE :routineID = routineDetailsId AND :exerciseID = exerciseDetailsId")
    fun getDetailOfRoutineExercise(routineID: Long, exerciseID: Long): Flow<MutableList<DetailsEntity>>

    @Query("SELECT * FROM details_table WHERE :routineID = routineDetailsId ")
    fun getDetailOfRoutine(routineID: Long): List<DetailsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetailToRoutineExercise(details: DetailsEntity)

    @Delete
    suspend fun deleteDetail(detail: DetailsEntity)

    @Update
    suspend fun updateDetail(detail: DetailsEntity)

}
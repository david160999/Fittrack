package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cursointermedio.myapplication.data.database.entities.DetailsEntity
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import kotlinx.coroutines.flow.Flow

interface DetailsDao {

    @Query("SELECT * FROM details_table WHERE :routineID = routineDetailsId AND :exerciseID = exerciseDetailsId")
    suspend fun getDetailOfRoutineExercise(routineID: Int, exerciseID: Int): Flow<List<DetailsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetailToRoutineExercise(details: DetailsEntity)

    @Delete
    suspend fun deleteDetail(detail: DetailsEntity)

    @Update
    suspend fun updateDetail(detail: DetailsEntity)

}
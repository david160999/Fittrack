package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.RoutineWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {


    @Query("SELECT * FROM routine_table WHERE :weekID = weekRoutineId")
    fun getRoutinesWeek(weekID: Long): Flow<MutableList<RoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineToWeek(routine: RoutineEntity): Long

    @Transaction
    @Query("SELECT * FROM routine_table WHERE routineId = :routineId")
    suspend fun getRoutineWithExercises(routineId: Long): RoutineWithExercises


}
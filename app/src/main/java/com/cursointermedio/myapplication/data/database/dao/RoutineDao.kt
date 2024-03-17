package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {


    @Query("SELECT * FROM routine_table WHERE :weekID = weekRoutineId")
    fun getRoutinesWeek(weekID: Int): Flow<MutableList<RoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineToWeek(routine: RoutineEntity)

}
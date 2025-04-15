package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import kotlinx.coroutines.flow.Flow

@Dao
interface WeekDao {

    @Query("SELECT * FROM week_table WHERE :trainingId = trainingWeekId")
    fun getWeeksTraining(trainingId:Long): Flow<List<WeekEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeekToTraining(week: WeekEntity): Long

    @Delete
    suspend fun deleteWeek(week: WeekEntity)

    @Query("SELECT * FROM week_table WHERE trainingWeekId = :trainingId")
    fun getAllWeeksWithRoutines(trainingId:Long): Flow<List<WeekWithRoutines>>

    @Query("SELECT * FROM week_table WHERE weekId = :weekId")
    fun getWeekWithRoutines(weekId:Long): WeekWithRoutines
}
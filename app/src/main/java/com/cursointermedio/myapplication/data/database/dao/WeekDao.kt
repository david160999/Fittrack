package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeekDao {

    @Query("SELECT * FROM week_table WHERE :trainingID = trainingWeekId")
    fun getWeeksTraining(trainingID:Int): Flow<MutableList<WeekEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeekToTraining(week: WeekEntity)

    @Delete
    suspend fun deleteWeek(week: WeekEntity)
}
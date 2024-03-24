package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import kotlinx.coroutines.flow.Flow

@Dao
interface WeekDao {

    @Query("SELECT * FROM week_table WHERE :trainingID = trainingWeekId")
    fun getWeeksTraining(trainingID:Int): Flow<List<WeekEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeekToTraining(week: WeekEntity)

    @Delete
    suspend fun deleteWeek(week: WeekEntity)

    @Query("SELECT * FROM week_table WHERE trainingWeekId = :trainingId")
    fun getWeeksWithRoutines(trainingId:Int): Flow<List<WeekWithRoutines>>
}
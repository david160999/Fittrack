package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import kotlinx.coroutines.flow.Flow

interface TrainingDao {

    @Query("SELECT * FROM training_table")
    suspend fun getAllTraining(): Flow<List<TrainingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraining(training: TrainingEntity)

    @Delete
    suspend fun deleteTraining(training: TrainingEntity)

}
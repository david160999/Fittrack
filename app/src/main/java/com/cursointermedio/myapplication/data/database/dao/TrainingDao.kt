package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.domain.model.TrainingModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    @Query("SELECT * FROM training_table")
    fun getAllTraining(): Flow<List<TrainingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraining(training: TrainingEntity): Long

    @Delete
    suspend fun deleteTraining(training: TrainingEntity)

    @Query("DELETE FROM training_table")
    suspend fun deleteAllTraining()

    @Transaction
    @Query("SELECT * FROM training_table WHERE trainingId = :trainingId ")
    suspend fun getTrainingWithWeeksAndRoutines(trainingId: Long): TrainingWithWeeksAndRoutines


    @Transaction
    @Query(
        """
    SELECT 
        t.trainingId,
        t.name,
        t.description,
        COUNT(DISTINCT w.weekId) AS numWeeks,
        (
            SELECT COUNT(*) FROM routine_table r
            WHERE r.weekRoutineId = (
                SELECT w2.weekId FROM week_table w2
                WHERE w2.trainingWeekId = t.trainingId
                ORDER BY w2.weekId DESC
                LIMIT 1
            )
        ) AS numRoutines
    FROM training_table t
    LEFT JOIN week_table w ON t.trainingId = w.trainingWeekId
    GROUP BY t.trainingId
    ORDER BY t.trainingId DESC
"""
    )
    fun getTrainingsWithWeekAndRoutineCounts(): Flow<List<TrainingsWithWeekAndRoutineCounts>>


    @Update
    suspend fun changeNameTraining(training: TrainingEntity)
}

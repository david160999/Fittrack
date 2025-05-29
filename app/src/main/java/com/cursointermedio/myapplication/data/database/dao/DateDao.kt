package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.cursointermedio.myapplication.data.database.entities.CategoryEntity
import com.cursointermedio.myapplication.data.database.entities.DateEntity
import com.cursointermedio.myapplication.data.database.entities.DateWithTrac
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.TracEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDate(dateEntity: DateEntity)

    @Query("SELECT * FROM date_table WHERE dateId = :dateId")
    suspend fun getDate(dateId: String): DateEntity?

    @Transaction
    @Query("SELECT * FROM date_table WHERE dateId = :dateId")
    suspend fun getDateWithTrac(dateId: String): DateWithTrac?

    @Query("SELECT * FROM date_table ORDER BY dateId")
    suspend fun getAllDates(): List<DateEntity>

    @Query("SELECT * FROM date_table WHERE note IS NOT NULL AND note != '' ORDER BY dateId")
    suspend fun getDatesWithNotes(): List<DateEntity>

    @Delete
    suspend fun deleteDate(dateEntity: DateEntity)

    //TracEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrac(tracEntity: TracEntity)

    @Query("SELECT * FROM trac_table WHERE dateId = :dateId")
    fun getTracByDateFlow(dateId: String): Flow<TracEntity?>

    @Update
    suspend fun updateTrac(tracEntity: TracEntity)

    @Query("SELECT dateId FROM date_table WHERE routineId = :routineId")
    fun getDatesFromRoutine(routineId:Long): Flow<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDateList(dateList:List<DateEntity>)

    @Delete
    suspend fun deleteTrac(trac: TracEntity)

}
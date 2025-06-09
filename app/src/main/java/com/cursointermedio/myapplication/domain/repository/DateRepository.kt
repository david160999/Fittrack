package com.cursointermedio.myapplication.domain.repository

import com.cursointermedio.myapplication.data.database.entities.DateEntity
import com.cursointermedio.myapplication.data.database.entities.DateWithTrac
import com.cursointermedio.myapplication.data.database.entities.TracEntity
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface DateRepository {
    suspend fun insertOrUpdateDate(date: DateEntity)

    suspend fun getDateWithTrac(dateId: String): DateWithTrac?

    suspend fun insertOrUpdateTrac(trac: TracEntity)

    suspend fun getDate(dateId: String): DateEntity?

    fun getDateFlow(dateId: String): Flow<DateEntity?>

    fun getTracByDateFlow(dateId: String): Flow<TracEntity?>

    suspend fun deleteDate(date: DateEntity)

    suspend fun updateNoteForDate(dateId: String, note: String)

    fun getDatesFromRoutine(routineId: Long): Flow<String>

    suspend fun insertDateList(dateList: List<DateEntity>)

    suspend fun deleteTrac(trac: TracEntity)

    suspend fun updateNote(dateId: String, note: String?)

    suspend fun updateBodyWeight(dateId: String, bodyWeight: Float?)

    suspend fun deleteNote(dateId: String)

    suspend fun deleteBodyWeight(dateId: String)

    suspend fun deleteRoutineCalendar(dateId: String)

}
package com.cursointermedio.myapplication.domain.repository

import com.cursointermedio.myapplication.data.database.entities.DateEntity
import com.cursointermedio.myapplication.data.database.entities.DateWithTrac
import com.cursointermedio.myapplication.data.database.entities.TracEntity
import com.cursointermedio.myapplication.domain.model.UserData

interface DateRepository {
    suspend fun insertOrUpdateDate(date: DateEntity)

    // Obtener una fecha con su Trac (relación 1:1)
    suspend fun getDateWithTrac(dateId: String): DateWithTrac?

    suspend fun insertOrUpdateTrac(trac: TracEntity)

    suspend fun getDate(dateId: String): DateEntity?

    suspend fun getTracByDate(dateId: String): TracEntity?

    suspend fun deleteDate(date: DateEntity)

    suspend fun updateNoteForDate(dateId: String, note: String)

    suspend fun getDatesFromRoutine(routineId:Long): String

}
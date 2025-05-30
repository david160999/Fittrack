package com.cursointermedio.myapplication.data.repository

import androidx.room.Query
import com.cursointermedio.myapplication.data.database.dao.DateDao
import com.cursointermedio.myapplication.data.database.entities.DateEntity
import com.cursointermedio.myapplication.data.database.entities.DateWithTrac
import com.cursointermedio.myapplication.data.database.entities.TracEntity
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.repository.DateRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class DateRepositoryImpl @Inject constructor(
    private val dateDao: DateDao,
) : DateRepository {
    override suspend fun insertOrUpdateDate(date: DateEntity) {
        dateDao.insertDate(date)
    }

    override suspend fun getDateWithTrac(dateId: String): DateWithTrac? {
        return dateDao.getDateWithTrac(dateId)
    }

    override suspend fun insertOrUpdateTrac(trac: TracEntity) {
        dateDao.insertTrac(trac)
    }

    override suspend fun getDate(dateId: String): DateEntity? {
        return dateDao.getDate(dateId)
    }

    override fun getDateFlow(dateId: String): Flow<DateEntity?> {
        return dateDao.getDateFlow(dateId)
    }

    override fun getTracByDateFlow(dateId: String): Flow<TracEntity?> {
        return dateDao.getTracByDateFlow(dateId)
    }

    override suspend fun deleteDate(date: DateEntity) {
        dateDao.deleteDate(date)
    }

    override suspend fun updateNoteForDate(dateId: String, note: String) {
        val date = dateDao.getDate(dateId)
        if (date != null) {
            val updated = date.copy(note = note)
            dateDao.insertDate(updated)
        }
    }

    override fun getDatesFromRoutine(routineId: Long): Flow<String> {
        return dateDao.getDatesFromRoutine(routineId)

    }

    override suspend fun insertDateList(dateList: List<DateEntity>) {
        dateDao.insertDateList(dateList)
    }

    override suspend fun deleteTrac(trac: TracEntity){
        dateDao.deleteTrac(trac)
    }

    override suspend fun updateNote(dateId: String, note: String?) {
        dateDao.updateNote(dateId, note)
    }

    override suspend fun updateBodyWeight(dateId: String, bodyWeight: Float?) {
        dateDao.updateBodyWeight(dateId, bodyWeight)
    }

    override suspend fun deleteNote(dateId: String) {
        dateDao.deleteNote(dateId)
    }

    override suspend fun deleteBodyWeight(dateId: String) {
        dateDao.deleteBodyWeight(dateId)
    }

}
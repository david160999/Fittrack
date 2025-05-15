package com.cursointermedio.myapplication.data.repository

import com.cursointermedio.myapplication.data.database.dao.DateDao
import com.cursointermedio.myapplication.data.database.entities.DateEntity
import com.cursointermedio.myapplication.data.database.entities.DateWithTrac
import com.cursointermedio.myapplication.data.database.entities.TracEntity
import com.cursointermedio.myapplication.domain.repository.DateRepository
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

    override suspend fun getTracByDate(dateId: String): TracEntity? {
        return dateDao.getTracByDate(dateId)
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

    override suspend fun getDatesFromRoutine(routineId: Long): String {
        return dateDao.getDatesFromRoutine(routineId)

    }


}
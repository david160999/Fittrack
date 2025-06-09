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
    // Inserta o actualiza una fecha (onConflict = REPLACE)
    override suspend fun insertOrUpdateDate(date: DateEntity) {
        dateDao.insertDate(date)
    }

    // Obtiene una fecha junto con su Trac relacionado (relación uno-a-uno o uno-a-muchos)
    override suspend fun getDateWithTrac(dateId: String): DateWithTrac? {
        return dateDao.getDateWithTrac(dateId)
    }

    // Inserta o actualiza un Trac
    override suspend fun insertOrUpdateTrac(trac: TracEntity) {
        dateDao.insertTrac(trac)
    }

    // Obtiene una fecha por ID
    override suspend fun getDate(dateId: String): DateEntity? {
        return dateDao.getDate(dateId)
    }

    // Obtiene una lista de fechas con sus Tracs correspondientes como Flow
    override fun getDateListFlow(dateList: List<String>): Flow<List<DateWithTrac?>> {
        return dateDao.getDateListFlow(dateList)
    }

    // Obtiene un Flow reactivo de DateEntity
    override fun getDateFlow(dateId: String): Flow<DateEntity?> {
        return dateDao.getDateFlow(dateId)
    }

    // Obtiene un Flow reactivo de Date con Trac
    override fun getDateWithTracFlow(dateId: String): Flow<DateWithTrac?> {
        return dateDao.getDateWithTracFlow(dateId)
    }

    // Obtiene un Flow del Trac correspondiente a una fecha
    override fun getTracByDateFlow(dateId: String): Flow<TracEntity?> {
        return dateDao.getTracByDateFlow(dateId)
    }

    // Elimina una fecha
    override suspend fun deleteDate(date: DateEntity) {
        dateDao.deleteDate(date)
    }

    // Actualiza la nota de una fecha
    override suspend fun updateNoteForDate(dateId: String, note: String) {
        val date = dateDao.getDate(dateId)
        if (date != null) {
            val updated = date.copy(note = note)
            dateDao.insertDate(updated)
        }
    }

    // Devuelve las fechas asociadas a una rutina como Flow
    override fun getDatesFromRoutine(routineId: Long): Flow<String> {
        return dateDao.getDatesFromRoutine(routineId)
    }

    // Inserta múltiples fechas a la vez
    override suspend fun insertDateList(dateList: List<DateEntity>) {
        dateDao.insertDateList(dateList)
    }

    // Elimina un Trac
    override suspend fun deleteTrac(trac: TracEntity) {
        dateDao.deleteTrac(trac)
    }

    // Actualiza nota directamente (más eficiente que obtener-copiar-insertar)
    override suspend fun updateNote(dateId: String, note: String?) {
        dateDao.updateNote(dateId, note)
    }

    // Actualiza el peso corporal de la fecha
    override suspend fun updateBodyWeight(dateId: String, bodyWeight: Float?) {
        dateDao.updateBodyWeight(dateId, bodyWeight)
    }

    // Elimina la nota de una fecha (usualmente lo deja en null o vacío)
    override suspend fun deleteNote(dateId: String) {
        dateDao.deleteNote(dateId)
    }

    // Elimina el peso corporal registrado para una fecha
    override suspend fun deleteBodyWeight(dateId: String) {
        dateDao.deleteBodyWeight(dateId)
    }

    // Borra el registro de rutina en calendario para esa fecha
    override suspend fun deleteRoutineCalendar(dateId: String) {
        dateDao.deleteRoutineCalendar(dateId)
    }
}
package com.cursointermedio.myapplication.data.repository

import com.cursointermedio.myapplication.data.database.dao.WeekDao
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeekRepository @Inject constructor(
    private val weekDao: WeekDao
) {
    /**
     * Obtiene las semanas asociadas a un entrenamiento específico desde la base de datos.
     */
    fun getWeeksTrainingFromDatabase(trainingID: Long): Flow<List<WeekModel>> {
        val response = weekDao.getWeeksTraining(trainingID)
        return response.map { list -> list.map { it.toDomain() } }
    }

    /**
     * Inserta una nueva semana en la base de datos para un entrenamiento.
     */
    suspend fun insertWeekToTraining(week: WeekEntity): Long {
        return weekDao.insertWeekToTraining(week)
    }

    // Elimina una semana de la base de datos.
    suspend fun deleteWeek(week: WeekModel) {
        weekDao.deleteWeek(week.toDatabase())
    }

    /**
     * Obtiene todas las semanas junto con sus rutinas asociadas para un entrenamiento.
     */
    fun getAllWeeksWithRoutines(trainingID: Long): Flow<List<WeekWithRoutinesModel>> {
        val response = weekDao.getAllWeeksWithRoutines(trainingID)
        return response.map { list -> list.map { it.toDomain() } }
    }

    /**
     * Obtiene una semana específica junto con sus rutinas asociadas.
     */
    fun getWeekWithRoutines(weekId: Long): WeekWithRoutinesModel {
        val response = weekDao.getWeekWithRoutines(weekId)
        return response.toDomain()
    }

    /**
     * Obtiene el nombre de un entrenamiento específico.
     */
    suspend fun getTrainingName(trainingId: Long): String {
        return weekDao.getTrainingName(trainingId)
    }
}
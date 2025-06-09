package com.cursointermedio.myapplication.domain.useCase

import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.data.database.entities.DateEntity
import com.cursointermedio.myapplication.data.database.entities.DateWithTrac
import com.cursointermedio.myapplication.data.database.entities.ExerciseDetailsCount
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.TracEntity
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.data.repository.DateRepositoryImpl
import com.cursointermedio.myapplication.data.repository.ExerciseRepository
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.RoutineModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetDateUseCase @Inject constructor(
    private val repository: DateRepositoryImpl,
) {

    // Inserta o actualiza una entidad Date en la base de datos.
    suspend fun insertOrUpdateDate(date: DateEntity) {
        repository.insertOrUpdateDate(date)
    }

    // Inserta o actualiza una entidad Trac en la base de datos.
    suspend fun insertOrUpdateTrac(trac: TracEntity) {
        repository.insertOrUpdateTrac(trac)
    }

    // Obtiene una entidad Date a partir de su ID.
    suspend fun getDate(dateId: String): DateEntity? {
        return repository.getDate(dateId)
    }

    // Obtiene un Flow con una lista de fechas y sus trazas asociadas para una lista de IDs.
    fun getDateListFlow(dateList: List<String>): Flow<List<DateWithTrac?>> {
        return repository.getDateListFlow(dateList)
    }

    // Obtiene un Flow con la entidad Date para un ID específico.
    fun getDateFlow(dateId: String): Flow<DateEntity?> {
        return repository.getDateFlow(dateId)
    }

    // Obtiene un Flow con la entidad Date y su Trac asociada para un ID específico.
    fun getDateWithTracFlow(dateId: String): Flow<DateWithTrac?> {
        return repository.getDateWithTracFlow(dateId)
    }

    // Obtiene un Flow con la entidad Trac asociada a una fecha.
    fun getTracByDateFlow(dateId: String): Flow<TracEntity?> {
        return repository.getTracByDateFlow(dateId)
    }

    // Obtiene un Flow que emite las fechas asociadas a una rutina específica.
    fun getDatesFromRoutine(routineId: Long): Flow<String> {
        return repository.getDatesFromRoutine(routineId)
    }

    // Inserta una lista de entidades Date en la base de datos.
    suspend fun insertDateList(dateList: List<DateEntity>) {
        repository.insertDateList(dateList)
    }

    // Elimina una entidad Trac de la base de datos.
    suspend fun deleteTrac(trac: TracEntity) {
        repository.deleteTrac(trac)
    }

    // Actualiza la nota asociada a una fecha.
    suspend fun updateNote(dateId: String, note: String?) {
        repository.updateNote(dateId, note)
    }

    // Actualiza el peso corporal asociado a una fecha.
    suspend fun updateBodyWeight(dateId: String, bodyWeight: Float?) {
        repository.updateBodyWeight(dateId, bodyWeight)
    }

    // Elimina la nota asociada a una fecha.
    suspend fun deleteNote(dateId: String) {
        repository.deleteNote(dateId)
    }

    // Elimina el peso corporal asociado a una fecha.
    suspend fun deleteBodyWeight(dateId: String) {
        repository.deleteBodyWeight(dateId)
    }

    // Elimina la información del calendario de rutina asociada a una fecha.
    suspend fun deleteRoutineCalendar(dateId: String) {
        repository.deleteRoutineCalendar(dateId)
    }
}
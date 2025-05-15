package com.cursointermedio.myapplication.domain.useCase

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
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDateUseCase @Inject constructor(
    private val repository: DateRepositoryImpl,
) {

    suspend fun insertOrUpdateDate(date: DateEntity) {
        repository.insertOrUpdateDate(date)
    }

    suspend fun getDateWithTrac(dateId: String): DateWithTrac? {
        return repository.getDateWithTrac(dateId)
    }

    suspend fun insertOrUpdateTrac(trac: TracEntity) {
        repository.insertOrUpdateTrac(trac)
    }

    suspend fun getDate(dateId: String): DateEntity? {
        return repository.getDate(dateId)
    }

    suspend fun getTracByDate(dateId: String): TracEntity? {
        return repository.getTracByDate(dateId)
    }

    suspend fun deleteDate(date: DateEntity) {
        repository.deleteDate(date)
    }

    suspend fun updateNoteForDate(dateId: String, note: String) {
        repository.updateNoteForDate(dateId, note)
    }

    suspend fun getDatesFromRoutine(routineId: Long): String {
        return repository.getDatesFromRoutine(routineId)

    }
}
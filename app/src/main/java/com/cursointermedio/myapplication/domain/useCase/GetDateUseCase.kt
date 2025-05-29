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

    fun getTracByDateFlow(dateId: String): Flow<TracEntity?>{
        return repository.getTracByDateFlow(dateId)
    }

    suspend fun deleteDate(date: DateEntity) {
        repository.deleteDate(date)
    }

    suspend fun updateNoteForDate(dateId: String, note: String) {
        repository.updateNoteForDate(dateId, note)
    }

    fun getDatesFromRoutine(routineId: Long): Flow<String> {
        return repository.getDatesFromRoutine(routineId)

    }

    suspend fun insertDateList(dateList: List<DateEntity>) {
        repository.insertDateList(dateList)
    }

    suspend fun deleteTrac(trac: TracEntity){
        repository.deleteTrac(trac)
    }
}
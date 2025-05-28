package com.cursointermedio.myapplication.data.repository

import com.cursointermedio.myapplication.data.database.dao.DetailsDao
import com.cursointermedio.myapplication.data.database.dao.ExerciseDao
import com.cursointermedio.myapplication.data.database.entities.DetailsEntity
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DetailsRepository @Inject constructor(
private val detailDao: DetailsDao
) {

     fun getDetailOfRoutine(routineId: Long): List<DetailModel> {
        val response = detailDao.getDetailOfRoutine(routineId)
        return response.map {  it.toDomain() }
    }

    suspend fun insertDetailToRoutineExercise(detail: DetailsEntity){
        detailDao.insertDetailToRoutineExercise(detail)
    }

    suspend fun updateDetailToRoutineExercise(detail: List<DetailsEntity>){
        detailDao.updateDetailToRoutineExercise(detail)
    }

    suspend fun getDetailOfRoutineAndExercise(routineId: Long, exerciseId: Long): List<DetailModel> {
        val response =  detailDao.getDetailOfRoutineAndExercise(routineId, exerciseId)
        return response.map {it.toDomain() }
    }

    fun getDetailOfRoutineAndExerciseFlow(routineId: Long, exerciseId: Long): Flow<List<DetailModel>> {
        return detailDao.getDetailOfRoutineAndExerciseFlow(routineId, exerciseId)
            .map { list -> list.map { it.toDomain() } }
    }

    suspend fun deleteLastDetail(routineId: Long, exerciseId: Long) {
        detailDao.deleteLastDetail(routineId = routineId, exerciseId= exerciseId)

    }
}
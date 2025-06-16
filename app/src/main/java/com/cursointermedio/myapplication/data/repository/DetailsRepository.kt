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

    // Devuelve los detalles de una rutina como modelo de dominio
    fun getDetailOfRoutine(routineId: Long): List<DetailModel> {
        val response = detailDao.getDetailOfRoutine(routineId)
        return response.map { it.toDomain() }
    }

    // Inserta un detalle para una rutina y ejercicio específico
    suspend fun insertDetailToRoutineExercise(detail: DetailsEntity) {
        detailDao.insertDetailToRoutineExercise(detail)
    }

    // Actualiza múltiples detalles (útil cuando se editan todos los sets de un ejercicio)
    suspend fun updateDetailToRoutineExercise(detail: DetailsEntity) {
        detailDao.updateDetailToRoutineExercise(detail)
    }

    // Obtiene todos los detalles de un ejercicio dentro de una rutina
    suspend fun getDetailOfRoutineAndExercise(
        routineId: Long,
        exerciseId: Long
    ): List<DetailModel> {
        val response = detailDao.getDetailOfRoutineAndExercise(routineId, exerciseId)
        return response.map { it.toDomain() }
    }

    // Flow para obtener los detalles de un ejercicio dentro de una rutina de forma reactiva
    fun getDetailOfRoutineAndExerciseFlow(
        routineId: Long,
        exerciseId: Long
    ): Flow<List<DetailModel>> {
        return detailDao.getDetailOfRoutineAndExerciseFlow(routineId, exerciseId)
            .map { list -> list.map { it.toDomain() } }
    }

    // Elimina el último detalle registrado para un ejercicio en una rutina
    suspend fun deleteLastDetail(routineId: Long, exerciseId: Long) {
        detailDao.deleteLastDetail(routineId = routineId, exerciseId = exerciseId)
    }
}
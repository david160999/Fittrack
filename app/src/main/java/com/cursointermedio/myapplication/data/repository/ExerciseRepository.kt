package com.cursointermedio.myapplication.data.repository

import android.util.Log
import com.cursointermedio.myapplication.data.database.dao.ExerciseDao
import com.cursointermedio.myapplication.data.database.entities.ExerciseDetailsCount
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {

    // Flow reactivo de todos los ejercicios en la base de datos
    fun getAllExercisesFromDatabase(): Flow<List<ExerciseModel>> {
        return exerciseDao.getAllExercises()
            .map { entities -> entities.map { it.toDomain() } }
    }

    // Inserta un ejercicio (ignora si ya existe)
    suspend fun insertExercise(exercise: ExerciseEntity) {
        exerciseDao.insertExercise(exercise)
    }

    // Inserta relación entre rutina y ejercicio
    suspend fun insertExerciseToRoutine(crossRef: RoutineExerciseCrossRef) {
        exerciseDao.insertExerciseToRoutine(crossRef)
    }

    // Obtiene ejercicios según categoría
    suspend fun getExercisesFromCategory(categoryId: Long): List<ExerciseModel> {
        return exerciseDao.getExercisesFromCategory(categoryId)
            .map { it.toDomain() }
    }

    // Devuelve todas las categorías como modelos de dominio
    suspend fun getCategories(): List<CategoryInfo> {
        val response = exerciseDao.getCategories()
        Log.d("DAO_TEST", "Categories from DAO: $response") // Útil para debug
        return response.map { it.toDomain() }
    }

    // Cantidad de ejercicios en una rutina (flow)
    fun getExerciseFromRoutineCountFlow(routineId: Long): Flow<Int> {
        return exerciseDao.getExerciseFromRoutineCountFlow(routineId)
    }

    // Cantidad de ejercicios en una rutina (directo)
    suspend fun getExerciseFromRoutineCount(routineId: Long): Int {
        return exerciseDao.getExerciseFromRoutineCount(routineId)
    }

    // Cuenta los detalles de un ejercicio en una rutina específica
    suspend fun getDetailCountFromExercise(exerciseId: Long, routineId: Long): Int {
        return exerciseDao.getDetailCountFromExercise(exerciseId, routineId)
    }

    // Obtiene las notas del crossref rutina-ejercicio
    suspend fun getNotesFromCrossRef(routineId: Long, exerciseId: Long): String? {
        return exerciseDao.getNotesFromCrossRef(routineId, exerciseId)
    }

    // Actualiza las notas del crossref rutina-ejercicio
    suspend fun updateNotesFromCrossRef(routineId: Long, exerciseId: Long, notes: String) {
        exerciseDao.updateNotesFromCrossRef(routineId, exerciseId, notes)
    }

    // Obtiene un ejercicio por ID y lo mapea a modelo de dominio
    suspend fun getExercise(exerciseId: Long): ExerciseModel {
        return exerciseDao.getExercise(exerciseId).toDomain()
    }
}
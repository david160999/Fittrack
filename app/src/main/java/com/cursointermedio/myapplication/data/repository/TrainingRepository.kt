package com.cursointermedio.myapplication.data.repository

import com.cursointermedio.myapplication.data.database.dao.TrainingDao
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.domain.exception.NetworkException
import com.cursointermedio.myapplication.domain.exception.TrainingNotFoundException
import com.cursointermedio.myapplication.domain.exception.TrainingUploadException
import com.cursointermedio.myapplication.domain.exception.UserNotFoundException
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.toDomain
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TrainingRepository @Inject constructor(
    private val trainingDao: TrainingDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    ) {
    // -------- LOCAL DATABASE --------

    // Devuelve todos los entrenamientos como flujo reactivo
    fun getAllTrainingsFromDatabase(): Flow<List<TrainingModel>> {
        return trainingDao.getAllTraining()
            .map { list -> list.map { it.toDomain() } }
    }

    // Inserta un entrenamiento y devuelve el ID
    suspend fun insertTraining(training: TrainingEntity): Long {
        return trainingDao.insertTraining(training)
    }

    // Elimina un entrenamiento
    suspend fun deleteTraining(training: TrainingEntity) {
        trainingDao.deleteTraining(training)
    }

    // Elimina todos los entrenamientos
    suspend fun deleteAll() {
        trainingDao.deleteAllTraining()
    }

    // Cambia el nombre (o cualquier otro campo editable) del entrenamiento
    suspend fun changeNameTraining(training: TrainingEntity) {
        trainingDao.changeNameTraining(training)
    }

    // Obtiene un entrenamiento con sus semanas y rutinas
    suspend fun getTrainingWithWeeksAndRoutines(trainingId: Long): TrainingWithWeeksAndRoutines {
        return trainingDao.getTrainingWithWeeksAndRoutines(trainingId)
    }

    // Devuelve lista con cantidad de semanas y rutinas (para mostrar resumen visual)
    fun getTrainingsWithWeekAndRoutineCounts(): Flow<List<TrainingsWithWeekAndRoutineCounts>> {
        return trainingDao.getTrainingsWithWeekAndRoutineCounts()
    }

    // -------- FIREBASE --------

    // Sube entrenamiento a Firebase con código único + UID
    suspend fun uploadTrainingData(trainingMapper: Map<String, Any?>, code: String): String? {
        return try {
            val user = auth.currentUser ?: throw UserNotFoundException("Usuario no autenticado")
            val uniqueCode = code + user.uid

            firestore.collection("trainingData")
                .document(uniqueCode)
                .set(trainingMapper)
                .await()

            uniqueCode
        } catch (e: Exception) {
            throw TrainingUploadException("Error al subir datos a Firebase: ${e.message}")
        }
    }

    // Descarga datos desde Firebase por código
    suspend fun downLoadTrainingData(code: String): DocumentSnapshot? {
        return try {
            val snapshot = firestore.collection("trainingData")
                .document(code)
                .get()
                .await()

            if (snapshot.exists()) {
                snapshot
            } else {
                throw TrainingNotFoundException("No se encontró ningún entrenamiento con el código '$code'")
            }
        } catch (e: FirebaseException) {
            throw NetworkException("Problema al acceder a Firebase", e)
        } catch (e: Exception) {
            throw Exception("Error inesperado al descargar datos", e)
        }
    }
}
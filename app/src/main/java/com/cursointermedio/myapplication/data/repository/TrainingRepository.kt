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
    fun getAllTrainingsFromDatabase(): Flow<List<TrainingModel>> {
        val response = trainingDao.getAllTraining()
        return response.map { it -> it.map { it.toDomain() } }
    }

    suspend fun insertTraining(training: TrainingEntity): Long {
        return trainingDao.insertTraining(training)
    }

    suspend fun deleteTraining(training: TrainingEntity) {
        trainingDao.deleteTraining(training)
    }


    suspend fun getTrainingWithWeeksAndRoutines(trainingId: Long): TrainingWithWeeksAndRoutines =
        trainingDao.getTrainingWithWeeksAndRoutines(trainingId)


    suspend fun deleteAll() {
        return trainingDao.deleteAllTraining()
    }

    suspend fun changeNameTraining(training: TrainingEntity) {
        trainingDao.changeNameTraining(training)
    }

    fun getTrainingsWithWeekAndRoutineCounts(): Flow<List<TrainingsWithWeekAndRoutineCounts>> {
        return trainingDao.getTrainingsWithWeekAndRoutineCounts()
    }

    //    FIREBASE
    suspend fun uploadTrainingData(trainingMapper: Map<String, Any?>, code: String): String? {
        return try {
            val user = auth.currentUser ?: throw UserNotFoundException("Usuario no autenticado")
            val uniqueCode = code + user.uid

            // Subir datos a Firebase
            firestore.collection("trainingData")
                .document(uniqueCode)  // Usamos el uniqueCode directamente
                .set(trainingMapper)
                .await()

            // Retornar el código único
            uniqueCode
        } catch (e: Exception) {
            throw TrainingUploadException("Error al subir datos a Firebase: ${e.message}")
        }
    }

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
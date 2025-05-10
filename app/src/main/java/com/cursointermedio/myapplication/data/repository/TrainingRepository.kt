package com.cursointermedio.myapplication.data.repository

import com.cursointermedio.myapplication.data.database.dao.TrainingDao
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.toDomain
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TrainingRepository @Inject constructor(
    private val trainingDao: TrainingDao,
    private val firestore: FirebaseFirestore

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
    suspend fun uploadTrainingData(trainingMapper: Map<String, Any?>, code: String) {
        try {
            firestore.collection("trainingData")
                .document(code)
                .set(trainingMapper)
                .await()
        } catch (e: Exception) {
            throw FirebaseException("Error al subir datos a Firebase: ${e.message}")
        }
    }

    suspend fun downLoadTrainingData(code: String): DocumentSnapshot?{
        return try {
            val snapshot = firestore.collection("trainingData")
                .document(code)
                .get()
                .await()

            if (snapshot.exists()) snapshot else null
        } catch (e: Exception) {
            throw FirebaseException("Error al descargar los datos a Firebase: ${e.message}")
        }
    }
}
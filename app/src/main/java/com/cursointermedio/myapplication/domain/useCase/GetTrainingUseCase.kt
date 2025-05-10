package com.cursointermedio.myapplication.domain.useCase

import android.util.Log
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.data.repository.DetailsRepository
import com.cursointermedio.myapplication.data.repository.RoutineRepository
import com.cursointermedio.myapplication.data.repository.TrainingRepository
import com.cursointermedio.myapplication.domain.mappers.TrainingMapper
import com.cursointermedio.myapplication.domain.model.DetailFirebaseModel
import com.cursointermedio.myapplication.domain.model.ExerciseFirebaseModel
import com.cursointermedio.myapplication.domain.model.RoutineFirebaseModel
import com.cursointermedio.myapplication.domain.model.TrainingFirebaseModel
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekFirebaseModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.UUID
import javax.inject.Inject

class GetTrainingUseCase @Inject constructor(
    private val repository: TrainingRepository,
    private val routineRepository: RoutineRepository,
    private val detailRepository: DetailsRepository,
    private val trainingMapper: TrainingMapper

) {
    operator fun invoke(): Flow<List<TrainingModel>> {
        return repository.getAllTrainingsFromDatabase()
    }

    suspend fun insertTraining(training: TrainingModel): Long {
        return repository.insertTraining(training.toDatabase())
    }

    suspend fun getTrainingWithWeeksAndRoutines(trainingId: Long): TrainingWithWeeksAndRoutines =
        repository.getTrainingWithWeeksAndRoutines(trainingId)

    suspend fun deleteTraining(training: TrainingModel) {
        repository.deleteTraining(training.toDatabase())
    }

    suspend fun deleteAll() {
        return repository.deleteAll()
    }

    suspend fun changeNameTraining(training: TrainingModel) {
        repository.changeNameTraining(training.toDatabase())
    }

    fun getTrainingsWithWeekAndRoutineCounts(): Flow<List<TrainingsWithWeekAndRoutineCounts>> {
        return repository.getTrainingsWithWeekAndRoutineCounts()
    }

    private fun generateUniqueCode(training: TrainingModel): String? {
        return try {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(training) // Convierte los datos en un String JSON

            val uniqueJson = json + UUID.randomUUID().toString()

            val md = MessageDigest.getInstance("SHA-256")  // Algoritmo de hash
            val hash = md.digest(uniqueJson.toByteArray())  // Genera el hash

            // Convierte el hash a un string hexadecimal
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: JsonSyntaxException) {
            Log.e("Error", "Error al convertir el objeto a JSON: ${e.message}")
            null
        } catch (e: NoSuchAlgorithmException) {
            Log.e("Error", "SHA-256 no soportado: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("Error", "Error al generar el código único: ${e.message}")
            null
        }

    }

    suspend fun uploadTrainingData(training: TrainingModel): String? {

        val mapper = trainingMapper.invoke(training.trainingId!!)

        val code = generateUniqueCode(training)
        if (code != null) {
            repository.uploadTrainingData(mapper, code)
        }
        return code
    }

    suspend fun downLoadTrainingData(code: String){
        val trainingData = repository.downLoadTrainingData(code)
        trainingMapper.fromFirebaseToLocalDatabase(trainingData)

    }

}

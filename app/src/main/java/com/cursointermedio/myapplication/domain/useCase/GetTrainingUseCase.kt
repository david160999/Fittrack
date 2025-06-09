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
    private val trainingMapper: TrainingMapper

) {
    // Operador invoke que emite un Flow con la lista de entrenamientos desde la base de datos.
    operator fun invoke(): Flow<List<TrainingModel>> {
        return repository.getAllTrainingsFromDatabase()
    }

    // Inserta un entrenamiento en la base de datos y devuelve el ID generado.
    suspend fun insertTraining(training: TrainingModel): Long {
        return repository.insertTraining(training.toDatabase())
    }

    // Obtiene un entrenamiento junto con sus semanas y rutinas asociadas.
    suspend fun getTrainingWithWeeksAndRoutines(trainingId: Long): TrainingWithWeeksAndRoutines =
        repository.getTrainingWithWeeksAndRoutines(trainingId)

    // Elimina un entrenamiento de la base de datos.
    suspend fun deleteTraining(training: TrainingModel) {
        repository.deleteTraining(training.toDatabase())
    }

    // Elimina todos los entrenamientos de la base de datos.
    suspend fun deleteAll() {
        return repository.deleteAll()
    }

    // Cambia el nombre de un entrenamiento existente.
    suspend fun changeNameTraining(training: TrainingModel) {
        repository.changeNameTraining(training.toDatabase())
    }

    // Emite un Flow con entrenamientos junto con la cantidad de semanas y rutinas que contienen.
    fun getTrainingsWithWeekAndRoutineCounts(): Flow<List<TrainingsWithWeekAndRoutineCounts>> {
        return repository.getTrainingsWithWeekAndRoutineCounts()
    }

    // Genera un código único para un entrenamiento a partir de su contenido serializado y hasheado con SHA-256.
    private fun generateUniqueCode(training: TrainingModel): String? {
        return try {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(training) // Convierte el objeto a JSON

            val md = MessageDigest.getInstance("SHA-256")  // Inicializa algoritmo de hash SHA-256
            val hash = md.digest(json.toByteArray())  // Calcula el hash del JSON

            // Convierte el hash en un string hexadecimal legible
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

    // Sube los datos de un entrenamiento a Firebase usando un código único generado a partir del entrenamiento.
    suspend fun uploadTrainingData(training: TrainingModel): String? {
        val mapper = trainingMapper.invoke(training.trainingId!!)
        val code = generateUniqueCode(training)
        var uniqueCode: String? = null

        if (code != null) {
            val result = repository.uploadTrainingData(mapper, code)
            uniqueCode = result
        }

        return uniqueCode
    }

    // Descarga los datos de entrenamiento desde Firebase usando un código único y los inserta en la base local.
    suspend fun downLoadTrainingData(code: String) {
        val trainingData = repository.downLoadTrainingData(code)
        if (trainingData != null) {
            trainingMapper.fromFirebaseToLocalDatabase(trainingData)
        }
    }
}

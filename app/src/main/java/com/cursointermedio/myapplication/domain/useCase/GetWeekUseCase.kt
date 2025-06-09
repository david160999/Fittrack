package com.cursointermedio.myapplication.domain.useCase

import android.util.Log
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.data.repository.WeekRepository
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetWeekUseCase @Inject constructor(
    private val repository: WeekRepository,
    private val getRoutineUseCase: GetRoutineUseCase,
    private val getExercisesUseCase: GetExercisesUseCase,
    private val getDetailsUseCase: GetDetailsUseCase

) {
    // Obtiene la lista de semanas asociadas a un entrenamiento, como Flow para observar cambios.
    operator fun invoke(trainingId: Long): Flow<List<WeekModel>> {
        return repository.getWeeksTrainingFromDatabase(trainingId)
    }

    // Inserta una nueva semana en un entrenamiento y devuelve el ID generado.
    suspend fun insertWeekToTraining(week: WeekModel): Long {
        return repository.insertWeekToTraining(week.toDatabase())
    }

    // Obtiene todas las semanas con sus rutinas asociadas, como Flow para observar cambios.
    fun getAllWeeksWithRoutines(trainingId: Long): Flow<List<WeekWithRoutinesModel>> =
        repository.getAllWeeksWithRoutines(trainingId)

    // Elimina una semana específica.
    suspend fun deleteWeek(week: WeekModel) {
        return repository.deleteWeek(week)
    }

    // Obtiene el nombre del entrenamiento según su ID.
    suspend fun getTrainingName(trainingId: Long): String {
        return repository.getTrainingName(trainingId)
    }

    // Obtiene una semana con todas sus rutinas (sin ser Flow).
    fun getWeekWithRoutines(weekId: Long): WeekWithRoutinesModel {
        return repository.getWeekWithRoutines(weekId)
    }

    // Copia una semana completa junto con sus rutinas, ejercicios y detalles, según la opción seleccionada.
    suspend fun createCopyOfWeek(
        weekIdOriginal: Long?,
        trainingWeekId: Long,
        optionSelected: CopyOption?,
    ) {
        withContext(Dispatchers.IO) {
            try {
                // Paso 1: Obtener la semana original con sus rutinas
                val semanaConRutinasOriginal = getWeekWithRoutines(weekIdOriginal!!)

                val semanaOriginal = semanaConRutinasOriginal.week
                val rutinasOriginales = semanaConRutinasOriginal.routineList

                // Paso 2: Crear una nueva semana con los datos de la original
                val nuevaSemana = WeekModel(
                    weekId = null,
                    trainingWeekId = trainingWeekId,
                    name = semanaOriginal.name,
                    description = semanaOriginal.description
                )
                val nuevoWeekId = insertWeekToTraining(nuevaSemana)

                // Paso 3: Copiar cada rutina y sus ejercicios
                rutinasOriginales.forEach { rutinaOriginal ->
                    val newRoutineId = getRoutineUseCase.copyRoutine(rutinaOriginal, nuevoWeekId)

                    // Copiar ejercicios asociados a la rutina copiada
                    getExercisesUseCase.copyExercise(rutinaOriginal.routineId!!, newRoutineId)

                    // Según la opción, copiar solo objetivos o todos los detalles
                    when (optionSelected) {
                        is CopyOption.CopyOnlyObjective -> getDetailsUseCase.copyOnlyObjectiveToNewWeek(
                            rutinaOriginal.routineId,
                            newRoutineId
                        )

                        is CopyOption.CopyAllDetails -> getDetailsUseCase.copyDetailsToNewWeek(
                            rutinaOriginal.routineId,
                            newRoutineId
                        )

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                // Captura y loguea errores ocurridos durante la copia
                Log.e("Error", "Ocurrió un error al intentar copiar la semana: ${e.message}")
            }
        }
    }
}

// Opciones para controlar qué detalles se copian al duplicar una semana.
sealed class CopyOption {
    data object CopyOnlyObjective : CopyOption()
    data object CopyAllDetails : CopyOption()
}
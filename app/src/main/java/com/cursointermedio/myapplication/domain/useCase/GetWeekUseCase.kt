package com.cursointermedio.myapplication.domain.useCase

import android.util.Log
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.data.repository.TrainingRepository
import com.cursointermedio.myapplication.data.repository.WeekRepository
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.domain.model.toDomain
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
    operator fun invoke(trainingId:Long): Flow<List<WeekModel>> {
        return repository.getWeeksTrainingFromDatabase(trainingId)
    }

    suspend fun insertWeekToTraining(week : WeekModel): Long {
        return repository.insertWeekToTraining(week.toDatabase())

    }

     fun getAllWeeksWithRoutines(trainingId: Long) : Flow<List<WeekWithRoutinesModel>> = repository.getAllWeeksWithRoutines(trainingId)


    suspend fun deleteWeek(week: WeekModel){
        return repository.deleteWeek(week)
    }

    suspend fun getWeekWithRoutines(weekId:Long): WeekWithRoutinesModel{
       return repository.getWeekWithRoutines(weekId)
    }

    suspend fun createCopyOfWeek(
        weekIdOriginal: Long?,
        trainingWeekId: Long,
        optionSelected: CopyOption?,

        ) {
        withContext(Dispatchers.IO) {
            try {
                // Paso 1: Obtener la semana con sus rutinas
                val semanaConRutinasOriginal = getWeekWithRoutines(weekIdOriginal!!)

                val semanaOriginal = semanaConRutinasOriginal.week
                val rutinasOriginales = semanaConRutinasOriginal.routineList

                // Paso 2: Crear una nueva semana
                val nuevaSemana = WeekModel(
                    weekId = null,
                    trainingWeekId = trainingWeekId,
                    name = semanaOriginal.name,
                    description = semanaOriginal.description
                )
                val nuevoWeekId = insertWeekToTraining(nuevaSemana)

                // Paso 3: Copiar cada rutina
                rutinasOriginales.forEach { rutinaOriginal ->
                    val newRoutineId = getRoutineUseCase.copyRoutine(rutinaOriginal, nuevoWeekId)

                    //Paso 4: Copiar ejercicios de esa rutina
                    getExercisesUseCase.copyExercise(rutinaOriginal.routineId!!, newRoutineId)

                    when (optionSelected) {
                        is CopyOption.CopyOnlyObjective -> getDetailsUseCase.copyOnlyObjectiveToNewWeek(
                            rutinaOriginal.routineId,
                            newRoutineId
                        )

                        is CopyOption.CopyAllDetails -> getDetailsUseCase.copyDetailsToNewWeek(
                            rutinaOriginal.routineId,
                            newRoutineId
                        )
                        null -> TODO()
                    }

                }
            } catch (e: Exception) {
                // Si ocurre un error (por ejemplo, un valor nulo o cualquier otro problema), lo capturamos aquí
                Log.e("Error", "Ocurrió un error al intentar copiar la semana: ${e.message}")
            }
        }
    }
}

sealed class CopyOption {
    object CopyOnlyObjective : CopyOption()
    object CopyAllDetails : CopyOption()
}
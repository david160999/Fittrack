package com.cursointermedio.myapplication.ui.week

import android.adservices.adid.AdId
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.domain.useCase.GetDetailsUseCase
import com.cursointermedio.myapplication.domain.useCase.GetExercisesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import com.cursointermedio.myapplication.domain.useCase.GetTrainingUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WeekViewModel @Inject constructor(
    private val getWeekUseCase: GetWeekUseCase,
    private val getRoutineUseCase: GetRoutineUseCase,
    private val getDetailUseCase: GetDetailsUseCase,
    private val getExercisesUseCase: GetExercisesUseCase
) : ViewModel() {


    fun getAllWeeksWithRoutines(trainingId: Long): StateFlow<List<WeekWithRoutinesModel>> =
        getWeekUseCase.getAllWeeksWithRoutines(trainingId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    suspend fun insertWeek(week: WeekModel) {
        getWeekUseCase.insertWeekToTraining(week)
    }

    suspend fun insertRoutine(routine: RoutineModel){
        getRoutineUseCase.insertRoutineToWeek(routine)
    }
    suspend fun createCopyOfWeek(
        weekIdOriginal: Long?,
        trainingWeekId: Long,
        optionSelected: String,

    ) {
        withContext(Dispatchers.IO) {
            try {
                // Paso 1: Obtener la semana con sus rutinas
                val semanaConRutinasOriginal = getWeekUseCase.getWeekWithRoutines(weekIdOriginal!!)

                val semanaOriginal = semanaConRutinasOriginal.week
                val rutinasOriginales = semanaConRutinasOriginal.routineList

                // Paso 2: Crear una nueva semana
                val nuevaSemana = WeekModel(
                    weekId = null,
                    trainingWeekId = trainingWeekId,
                    name = semanaOriginal.name,
                    description = semanaOriginal.description
                )
                val nuevoWeekId = getWeekUseCase.insertWeekToTraining(nuevaSemana)

                // Paso 3: Copiar cada rutina
                rutinasOriginales.forEach { rutinaOriginal ->
                    val newRoutineId = copyRoutine(rutinaOriginal, nuevoWeekId)

                    //Paso 4: Copiar ejercicios de esa rutina
                    copyExercise(rutinaOriginal.routineId!!, newRoutineId)

                    when (optionSelected) {
                        "CopyWeekWithObj" -> copyOnlyObjectiveToNewWeek(
                            rutinaOriginal.routineId,
                            newRoutineId
                        )

                        "CopyWeekWithAll" -> copyDetailsToNewWeek(
                            rutinaOriginal.routineId,
                            newRoutineId
                        )
                    }

                }
            } catch (e: Exception) {
                // Si ocurre un error (por ejemplo, un valor nulo o cualquier otro problema), lo capturamos aquí
                Log.e("Error", "Ocurrió un error al intentar copiar la semana: ${e.message}")
            }
        }
    }

    private suspend fun copyRoutine(rutinaOriginal: RoutineEntity, nuevoWeekId: Long): Long {
        val nuevaRutina = RoutineModel(
            routineId = null,
            weekRoutineId = nuevoWeekId,
            name = rutinaOriginal.name,
            description = rutinaOriginal.description
        )
        return getRoutineUseCase.insertRoutineToWeek(nuevaRutina)
    }

    private suspend fun copyExercise(routineId: Long, newRoutineId: Long) {
        val exercises = getRoutineUseCase.getRoutineWithExercises(routineId)
        exercises.exercises.forEach() { exercise ->
            val relation = RoutineExerciseCrossRef(
                routineId = newRoutineId,  // ID de la rutina
                exerciseId = exercise.exerciseId!!  // ID del ejercicio
            )
            getExercisesUseCase.insertExerciseToRoutine(relation)
        }
    }

    private suspend fun copyDetailsToNewWeek(routineId: Long, newRoutineId: Long) {
        val detalles = getDetailUseCase.getDetailOfRoutine(routineId)

        detalles.forEach() { detalle ->
            val nuevoDetalle = DetailModel(
                detailsId = 0,
                routineDetailsId = newRoutineId,
                exerciseDetailsId = detalle.exerciseDetailsId,
                realWeight = detalle.realWeight,
                realReps = detalle.realReps,
                realRpe = detalle.realRpe,
                objWeight = detalle.objWeight,
                objReps = detalle.objReps,
                objRpe = detalle.objRpe
            )
            getDetailUseCase.insertDetailToRoutineExercise(nuevoDetalle)
        }

    }

    private suspend fun copyOnlyObjectiveToNewWeek(routineId: Long, newRoutineId: Long) {
        val detalles = getDetailUseCase.getDetailOfRoutine(routineId)

        detalles.forEach() { detalle ->
            val nuevoDetalle = DetailModel(
                detailsId = 0,
                routineDetailsId = newRoutineId,
                exerciseDetailsId = detalle.exerciseDetailsId,
                realWeight = null,
                realReps = null,
                realRpe = null,
                objWeight = detalle.objWeight,
                objReps = detalle.objReps,
                objRpe = detalle.objRpe
            )
            getDetailUseCase.insertDetailToRoutineExercise(nuevoDetalle)
        }

    }


}
package com.cursointermedio.myapplication.ui.routine

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.data.database.entities.ExerciseDetailsCount
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.RoutineWithExercises
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.useCase.GetExercisesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val getRoutineUseCase: GetRoutineUseCase,
    private val getExercisesUseCase: GetExercisesUseCase,
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {

    val routineId: Long = savedStateHandle.get<Long>("id") ?: -1L

    private val _routineWithExercise = MutableStateFlow<RoutineUiState>(RoutineUiState.Loading)
    val routineWithExercise: StateFlow<RoutineUiState> = _routineWithExercise


    init {
        viewModelScope.launch {
            getRoutineUseCase.getRoutineWithOrderedExercisesFlow(routineId)
                .flowOn(Dispatchers.IO)
                .catch { e -> _routineWithExercise.value = RoutineUiState.Error(e.message ?: "Ha ocurrido un error inesperado.") }
                .collectLatest { routine ->
                    val updatedExercises = routine.exercises.mapNotNull { exercise ->
                        val exerciseId = exercise.id
                        val routineId = routine.routine.routineId

                        if (exerciseId != null && routineId != null) {
                            val detailCount = getExercisesUseCase.getDetailCountFromExercise(
                                exerciseId, routineId
                            )
                            exercise.copy(detailCount = detailCount)
                        } else {
                            null // Ignora si falta un ID
                        }
                    }
                    _routineWithExercise.value = RoutineUiState.Success(routine.copy(exercises = updatedExercises))
                }
        }
    }


    fun deleteRoutine(exercise: ExerciseModel) {
        viewModelScope.launch {
            try {
                if (exercise.id != null) {
                    val crossRef = RoutineExerciseCrossRef(
                        routineId = routineId,
                        exerciseId = exercise.id,
                        order = null,
                        notes = null
                    )
                    getRoutineUseCase.removeExerciseFromRoutine(crossRef)
                }
            } catch (e: Exception) {
                Log.e("DeleteRoutine", "Error al eliminar ejercicio de rutina", e)

            }
        }


    }

    fun changeOrderRoutines(exercise :List<ExerciseModel>) {
        viewModelScope.launch {
            try {
                exercise.mapIndexed { index, exercise->
                    if (exercise.id != null){
                        getRoutineUseCase.updateOrderCrossRefRoutineExercise(exercise.id, routineId, index)
                        Log.e("REF", exercise.id.toString() + routineId.toString() + index)
                    }
                }
            } catch (e: Exception) {
                Log.e("ChangeOrderRoutines", "Error al updatear el orden de los ejercicios", e)
            }
        }
    }
}
package com.cursointermedio.myapplication.ui.routine

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.domain.model.ExerciseModel
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

// ViewModel para gestionar la lógica de una rutina y sus ejercicios asociados.
// Permite observar los ejercicios de una rutina, eliminarlos y actualizar su orden.
@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val getRoutineUseCase: GetRoutineUseCase,       // Caso de uso para obtener y modificar rutinas
    private val getExercisesUseCase: GetExercisesUseCase,   // Caso de uso para obtener datos de ejercicios
    private val savedStateHandle: SavedStateHandle          // Manejador de estado para recuperar argumentos (ID de rutina)
) : ViewModel() {

    // ID de la rutina que se está mostrando (obtenido por navegación)
    val routineId: Long = savedStateHandle.get<Long>("id") ?: -1L

    // Estado observable de la rutina y sus ejercicios (Loading, Success, Error)
    private val _routineWithExercise = MutableStateFlow<RoutineUiState>(RoutineUiState.Loading)
    val routineWithExercise: StateFlow<RoutineUiState> = _routineWithExercise

    init {
        // Al iniciar el ViewModel, observa la rutina y sus ejercicios en tiempo real
        viewModelScope.launch {
            getRoutineUseCase.getRoutineWithOrderedExercisesFlow(routineId)
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    // Si ocurre un error, lo refleja en el estado
                    _routineWithExercise.value = RoutineUiState.Error(e.message ?: "Ha ocurrido un error inesperado.")
                }
                .collectLatest { routine ->
                    // Por cada ejercicio, obtiene el conteo de detalles y lo añade al modelo
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
                    // Emite el estado actualizado con los ejercicios enriquecidos
                    _routineWithExercise.value = RoutineUiState.Success(routine.copy(exercises = updatedExercises))
                }
        }
    }

    // Elimina un ejercicio de la rutina
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

    // Actualiza el orden de los ejercicios en la rutina después de un drag & drop
    fun changeOrderRoutines(exercise: List<ExerciseModel>) {
        viewModelScope.launch {
            try {
                exercise.mapIndexed { index, exercise ->
                    if (exercise.id != null) {
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
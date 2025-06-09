package com.cursointermedio.myapplication.ui.addExercise

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.RoutineWithExercises
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.useCase.GetExercisesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import com.cursointermedio.myapplication.ui.routine.RoutineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddExerciseViewModel @Inject constructor(
    private val getExercisesUseCase: GetExercisesUseCase,
) : ViewModel() {

    // Estado para la lista de categorías, expuesto como StateFlow inmutable
    private val _categoryList = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val categoryList: StateFlow<CategoryUiState> = _categoryList

    // Estado para la lista de ejercicios, expuesto como StateFlow inmutable
    private val _exerciseList = MutableStateFlow<AddExerciseUiState>(AddExerciseUiState.Loading)
    val exerciseList: StateFlow<AddExerciseUiState> = _exerciseList

    init {
        // Al inicializar el ViewModel, cargamos todos los ejercicios y categorías
        getAllExercise()
        getCategories()
    }

    /**
     * Obtiene todos los ejercicios desde la base de datos usando el caso de uso.
     * Actualiza el estado _exerciseList según el resultado.
     */
    fun getAllExercise() {
        viewModelScope.launch {
            getExercisesUseCase.getAllExercisesFromDatabase()
                .flowOn(Dispatchers.IO) // Ejecutar en hilo IO para no bloquear UI
                .catch { e ->
                    // Captura errores y actualiza estado con mensaje de error
                    _exerciseList.value =
                        AddExerciseUiState.Error(e.message ?: "Ha ocurrido un error inesperado.")
                }
                .collectLatest { exercises ->
                    // Emitir la lista de ejercicios cuando llega
                    _exerciseList.value = AddExerciseUiState.Success(exercises)
                }
        }
    }

    /**
     * Obtiene los ejercicios filtrados por categoría.
     * Actualiza el estado _exerciseList con los ejercicios correspondientes o error.
     */
    fun getExercisesFromCategory(categoryId: Long) {
        viewModelScope.launch {
            _exerciseList.value = AddExerciseUiState.Loading
            try {
                val exercise = getExercisesUseCase.getExercisesFromCategory(categoryId)
                _exerciseList.value = AddExerciseUiState.Success(exercise)
            } catch (e: Exception) {
                _exerciseList.value =
                    AddExerciseUiState.Error(e.message ?: "Ha ocurrido un error inesperado.")
            }
        }
    }

    /**
     * Obtiene la lista de categorías disponibles.
     * Actualiza el estado _categoryList con los datos o error.
     */
    private fun getCategories() {
        viewModelScope.launch {
            _categoryList.value = CategoryUiState.Loading
            try {
                val categories = getExercisesUseCase.getCategories()
                _categoryList.value = CategoryUiState.Success(categories)
            } catch (e: Exception) {
                _categoryList.value =
                    CategoryUiState.Error(e.message ?: "Ha ocurrido un error inesperado.")
            }
        }
    }

    /**
     * Inserta los ejercicios seleccionados en una rutina dada.
     * Cada ejercicio se añade con un orden y sin notas por defecto.
     */
    fun insertExerciseToRoutine(
        routineId: Long,
        selectedExercises: List<ExerciseModel>
    ) {
        viewModelScope.launch {
            try {
                selectedExercises.forEachIndexed { index, exercise ->
                    val crossRef = RoutineExerciseCrossRef(
                        routineId = routineId,
                        exerciseId = exercise.id!!, // Aseguramos que el ID no sea nulo
                        order = index,
                        notes = null
                    )
                    getExercisesUseCase.insertExerciseToRoutine(crossRef)
                }
            } catch (e: Exception) {
                Log.e("insertExerciseToRoutine", "Error al insertar ejercicios a la rutina", e)
            }
        }
    }

    /**
     * Inserta un nuevo ejercicio en la base de datos.
     */
    fun insertExercise(exercise: ExerciseModel) {
        viewModelScope.launch {
            try {
                getExercisesUseCase.insertExercise(exercise)
            } catch (e: Exception) {
                Log.e("insertExercise", "Error al insertar ejercicio", e)
            }
        }
    }
}
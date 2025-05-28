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
    private val _categoryList = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val categoryList: StateFlow<CategoryUiState> = _categoryList

    private val _exerciseList = MutableStateFlow<AddExerciseUiState>(AddExerciseUiState.Loading)
    val exerciseList: StateFlow<AddExerciseUiState> = _exerciseList

    init {
        getAllExercise()
        getCategories()
    }

    fun getAllExercise() {
        viewModelScope.launch {
            getExercisesUseCase.getAllExercisesFromDatabase()
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    _exerciseList.value =
                        AddExerciseUiState.Error(e.message ?: "Ha ocurrido un error inesperado.")
                }
                .collectLatest { exercises ->
                    _exerciseList.value = AddExerciseUiState.Success(exercises)
                }
        }

    }

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

    fun insertExerciseToRoutine(
        routineId: Long,
        selectedExercises: List<ExerciseModel>
    ) {
        viewModelScope.launch {
            try {
                selectedExercises.forEachIndexed { index, exercise ->
                    val crossRef = RoutineExerciseCrossRef(
                        routineId = routineId,
                        exerciseId = exercise.id!!,
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

    fun insertExercise(exercise: ExerciseModel) {
        viewModelScope.launch {
            try {
                getExercisesUseCase.insertExercise(exercise)

            } catch (e: Exception) {
                Log.e("insertExercise", "Error al insertar ejercicios a la rutina", e)

            }
        }
    }
}
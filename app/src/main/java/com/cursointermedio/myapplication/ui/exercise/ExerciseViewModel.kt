package com.cursointermedio.myapplication.ui.exercise

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.useCase.GetDetailsUseCase
import com.cursointermedio.myapplication.domain.useCase.GetExercisesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import com.cursointermedio.myapplication.ui.routine.RoutineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val getDetailsUseCase: GetDetailsUseCase,
    private val getExercisesUseCase: GetExercisesUseCase,
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {

    val exerciseId: Long = savedStateHandle.get<Long>("exerciseId") ?: -1L
    val routineId: Long = savedStateHandle.get<Long>("routineId") ?: -1L

    private val _notes = MutableStateFlow<String?>(null)
    val notes: StateFlow<String?> = _notes

    private val _adapterFragment = MutableStateFlow(1)
    val adapterFragment: StateFlow<Int> = _adapterFragment

    private val _exercise = MutableStateFlow<ExerciseModel?>(null)
    val exercise: StateFlow<ExerciseModel?> = _exercise

    private val _detailList = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailList: StateFlow<DetailUiState> = _detailList

    private val _detailResponseList = MutableStateFlow<List<DetailModel>>(emptyList())
    val detailResponseList: StateFlow<List<DetailModel>> = _detailResponseList

    init {
        getDetailOfRoutineAndExerciseFlow()
        getNotesFromCrossRef()
        getExercise()
    }

    private fun getDetailOfRoutineAndExerciseFlow() {
        viewModelScope.launch {
            getDetailsUseCase.getDetailOfRoutineAndExerciseFlow(
                routineId = routineId,
                exerciseId = exerciseId
            )
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    _detailList.value =
                        DetailUiState.Error(e.message ?: "Ha ocurrido un error inesperado.")
                }
                .collectLatest { detailList ->
                    _detailList.value = DetailUiState.Success(detailList)
                    _detailResponseList.value = detailList
                }
        }
    }

    fun insertDetailToRoutineExercise() {
        viewModelScope.launch {
            try {
                val newDetail = DetailModel(
                    null,
                    routineDetailsId = routineId,
                    exerciseDetailsId = exerciseId,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
                getDetailsUseCase.insertDetailToRoutineExercise(newDetail)

            } catch (e: Exception) {
                Log.e("insertDetailToRoutineExercise", "Error al insertar detalles", e)
                Log.e(
                    "insertDetailToRoutineExercise",
                    routineId.toString() + exerciseId.toString(),
                    e
                )

            }
        }
    }

    fun updateDetailToRoutineExercise(detail: List<DetailModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getDetailsUseCase.updateDetailToRoutineExercise(detail)

            } catch (e: Exception) {
                Log.e("updateDetailToRoutineExercise", "Error al actualizar los detalles", e)
            }
        }
    }

    fun deleteLastDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getDetailsUseCase.deleteLastDetail(routineId = routineId, exerciseId = exerciseId)

            } catch (e: Exception) {
                Log.e("updateDetailToRoutineExercise", "Error al actualizar los detalles", e)
            }
        }
    }

    private fun getNotesFromCrossRef() {
        viewModelScope.launch {
            try {
                _notes.value = getExercisesUseCase.getNotesFromCrossRef(
                    routineId = routineId,
                    exerciseId = exerciseId
                )
            } catch (e: Exception) {
                Log.e("getNotesFromCrossRef", "Error cargar las notas del ejercicio", e)
            }
        }
    }

    fun updateNotesFromCrossRef(notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getExercisesUseCase.updateNotesFromCrossRef(
                    routineId = routineId,
                    exerciseId = exerciseId,
                    notes
                )
            } catch (e: Exception) {
                Log.e("updateNotesFromCrossRef", "Error al actualizar las notas del ejercicio", e)
            }
        }
    }

    private fun getExercise() {
        viewModelScope.launch {
            try {
                _exercise.value = getExercisesUseCase.getExercise(exerciseId)
            } catch (e: Exception) {
                Log.e("updateNotesFromCrossRef", "Error al actualizar las notas del ejercicio", e)
            }
        }
    }

    fun updateList(updatedItem: DetailModel) {
        val updatedList = _detailResponseList.value.map {
            if (it.detailsId == updatedItem.detailsId) updatedItem else it
        }
        _detailResponseList.value = updatedList
    }

    fun changeFragmentAdapter(fragment: Int) {
        when (fragment) {
            0 -> {_adapterFragment.value = 0}
            1 -> {_adapterFragment.value = 1}
            else -> {}
        }
    }
}
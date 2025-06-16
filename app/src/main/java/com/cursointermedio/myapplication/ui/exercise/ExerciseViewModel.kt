package com.cursointermedio.myapplication.ui.exercise

import android.util.Log
import android.util.Pair
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.useCase.GetDetailsUseCase
import com.cursointermedio.myapplication.domain.useCase.GetExercisesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetUserPreferencesUseCase
import com.cursointermedio.myapplication.utils.extensions.E1RMFormulas
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val getDetailsUseCase: GetDetailsUseCase,
    private val getExercisesUseCase: GetExercisesUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase

) : ViewModel() {

    // Estado para las estadísticas del ejercicio (tonelaje y 1ERM)
    private val _exerciseStatistics = MutableStateFlow(Pair("", ""))
    val exerciseStatistics: StateFlow<Pair<String, String>> get() = _exerciseStatistics

    // IDs obtenidas del estado guardado (por ejemplo, argumentos del fragmento)
    val exerciseId: Long = savedStateHandle.get<Long>("exerciseId") ?: -1L
    val routineId: Long = savedStateHandle.get<Long>("routineId") ?: -1L

    // Estado para las notas del ejercicio
    private val _notes = MutableStateFlow<String?>(null)
    val notes: StateFlow<String?> = _notes

    // Estado para el fragmento actual del adaptador (0 = real, 1 = objetivo)
    private val _adapterFragment = MutableStateFlow(1)
    val adapterFragment: StateFlow<Int> = _adapterFragment

    // Estado para el ejercicio actual
    private val _exercise = MutableStateFlow<ExerciseModel?>(null)
    val exercise: StateFlow<ExerciseModel?> = _exercise

    // Estado para la lista de detalles (UIState: Success/Loading/Error)
    private val _detailList = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailList: StateFlow<DetailUiState> = _detailList

    init {
        getDetailOfRoutineAndExerciseFlow() // Carga detalles del ejercicio en la rutina
        getNotesFromCrossRef()              // Carga notas asociadas al ejercicio
        getExercise()                       // Carga información del ejercicio
    }

    /**
     * Obtiene y observa los detalles de la rutina y el ejercicio,
     * además de las preferencias del usuario para mostrar unidades correctas.
     * Actualiza el estado de la lista de detalles, las estadísticas y la respuesta del adaptador.
     */
    private fun getDetailOfRoutineAndExerciseFlow() {
        viewModelScope.launch {
            combine(
                getDetailsUseCase.getDetailOfRoutineAndExerciseFlow(
                    routineId = routineId,
                    exerciseId = exerciseId
                ),
                getUserPreferencesUseCase.userSettingsFlow
            ) { detail, userSettings ->
                // Determina unidad de peso según preferencia del usuario
                val unit = if (userSettings.isExerciseKgMode) "kg" else "lbs"
                // Suma total de tonelaje (peso real * repeticiones reales)
                val weightTotal = detail.sumOf {
                    (it.realWeight ?: 0) * (it.realReps ?: 0)
                }
                // Suma total de 1ERM usando la fórmula de Brzycki
                var weight1ERM = detail.sumOf {
                    E1RMFormulas.brzycki(it.realWeight ?: 0, it.realReps ?: 0)
                }
                weight1ERM = BigDecimal(weight1ERM).setScale(2, RoundingMode.HALF_UP).toDouble()

                Triple(detail, "$weightTotal $unit", "$weight1ERM $unit")
            }
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    // Manejo de errores al cargar detalles
                    _detailList.value = DetailUiState.Error(e.message ?: "Ha ocurrido un error inesperado.")
                }
                .collectLatest { (detailList, formattedWeight, weight1ERM) ->
                    _detailList.value = DetailUiState.Success(detailList)
                    _exerciseStatistics.value = Pair(formattedWeight, weight1ERM)
                }
        }
    }

    /**
     * Inserta un nuevo detalle vacío en la rutina y el ejercicio actuales.
     */
    fun insertDetailToRoutineExercise() {
        viewModelScope.launch {
            try {
                val newDetail = DetailModel(
                    null,             // detailsId
                    routineDetailsId = routineId,
                    exerciseDetailsId = exerciseId,
                    null, null, null, null, null, null // otros campos nulos
                )
                getDetailsUseCase.insertDetailToRoutineExercise(newDetail)
            } catch (e: Exception) {
                Log.e("insertDetailToRoutineExercise", "Error al insertar detalles", e)
            }
        }
    }

    /**
     * Actualiza la lista de detalles en la base de datos.
     */
    fun updateDetailToRoutineExercise(detail: DetailModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getDetailsUseCase.updateDetailToRoutineExercise(detail)
            } catch (e: Exception) {
                Log.e("updateDetailToRoutineExercise", "Error al actualizar los detalles", e)
            }
        }
    }

    /**
     * Elimina el último detalle registrado para la rutina y ejercicio actuales.
     */
    fun deleteLastDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getDetailsUseCase.deleteLastDetail(routineId = routineId, exerciseId = exerciseId)
            } catch (e: Exception) {
                Log.e("updateDetailToRoutineExercise", "Error al actualizar los detalles", e)
            }
        }
    }

    /**
     * Carga las notas asociadas a este ejercicio en la rutina.
     */
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

    /**
     * Actualiza las notas asociadas a este ejercicio en la rutina.
     */
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

    /**
     * Carga el modelo de ejercicio actual usando su ID.
     */
    private fun getExercise() {
        viewModelScope.launch {
            try {
                _exercise.value = getExercisesUseCase.getExercise(exerciseId)
            } catch (e: Exception) {
                Log.e("updateNotesFromCrossRef", "Error al actualizar las notas del ejercicio", e)
            }
        }
    }

    /**
     * Cambia el fragmento del adaptador (0 para Real, 1 para Objetivo).
     */
    fun changeFragmentAdapter(fragment: Int) {
        when (fragment) {
            0 -> {
                _adapterFragment.value = 0
            }
            1 -> {
                _adapterFragment.value = 1
            }
            else -> {}
        }
    }
}
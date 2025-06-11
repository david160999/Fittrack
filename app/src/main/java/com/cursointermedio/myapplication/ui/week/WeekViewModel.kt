package com.cursointermedio.myapplication.ui.week

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.data.database.entities.DateEntity
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.domain.useCase.CopyOption
import com.cursointermedio.myapplication.domain.useCase.GetDateUseCase
import com.cursointermedio.myapplication.domain.useCase.GetExercisesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

// ViewModel encargado de manejar la lógica y los estados relacionados con las semanas y sus rutinas dentro de un entrenamiento.
// Gestiona la obtención, actualización, copiado, borrado y asignación de fechas a rutinas, así como la generación de la lista de semanas para el spinner.

@HiltViewModel
class WeekViewModel @Inject constructor(
    private val getWeekUseCase: GetWeekUseCase,
    private val getRoutineUseCase: GetRoutineUseCase,
    private val getExercisesUseCase: GetExercisesUseCase,
    private val getDateUseCase: GetDateUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ID del entrenamiento obtenido desde los argumentos de navegación
    val trainingId: Long = savedStateHandle.get<Long>("id") ?: -1L

    // Estado de la UI para la lista de semanas y rutinas (loading, éxito, error)
    private val _weeksWithRoutines = MutableStateFlow<WeekUiState>(WeekUiState.Loading)
    val weeksWithRoutines: StateFlow<WeekUiState> = _weeksWithRoutines

    // Lista de semanas con sus rutinas
    private val _weeks = MutableStateFlow<List<WeekWithRoutinesModel>>(emptyList())
    val weeks: StateFlow<List<WeekWithRoutinesModel>> = _weeks

    // Lista para el spinner de selección de semana (ej: "Semana 1", "Semana 2", ...)
    private val _spinnerList = MutableStateFlow<List<String>>(emptyList())
    val spinnerList: StateFlow<List<String>> = _spinnerList

    // Nombre del entrenamiento
    private val _trainingName = MutableStateFlow("")
    val trainingName: StateFlow<String> = _trainingName

    init {
        // Obtener el nombre del entrenamiento al iniciar
        viewModelScope.launch {
            getTrainingName()
        }

        // Observar las semanas y rutinas asociadas al entrenamiento y sus cambios en ejercicios y fechas
        @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
        viewModelScope.launch {
            getWeekUseCase.getAllWeeksWithRoutines(trainingId)
                .flatMapLatest { weeks ->
                    // Para cada semana, combinar los flujos de ejercicios y fechas de sus rutinas
                    val weekFlows = weeks.map { week ->
                        val routineFlows = week.routineList.map { routine ->
                            combine(
                                getExercisesUseCase.getExerciseFromRoutineCountFlow(routine.routineId!!),
                                getDateUseCase.getDatesFromRoutine(routine.routineId)
                            ) { numExercise, date ->
                                routine.copy(exerciseCount = numExercise, date = date)
                            }
                        }
                        if (routineFlows.isNotEmpty()) {
                            combine(routineFlows) { updatedRoutines ->
                                val orderedRoutines = updatedRoutines.sortedBy { it.order }
                                week.copy(routineList = orderedRoutines)
                            }
                        } else {
                            // Si no hay rutinas, devolvemos un flow con la semana sin cambios
                            flowOf(week)
                        }
                    }

                    combine(weekFlows) { updatedWeeks -> updatedWeeks.toList() }
                }
                .flowOn(Dispatchers.IO)
                .catch { e -> _weeksWithRoutines.value = WeekUiState.Error(e.message ?: "Error") }
                .collectLatest { updatedWeeks ->
                    _spinnerList.value = List(updatedWeeks.size) { index -> "Semana ${index + 1}" }
                    _weeks.value = updatedWeeks
                    _weeksWithRoutines.value = WeekUiState.Success(updatedWeeks)
                }
        }
    }

    // Obtiene y actualiza el nombre del entrenamiento
    private fun getTrainingName() {
        viewModelScope.launch {
            _trainingName.value = getWeekUseCase.getTrainingName(trainingId) + " <>"
        }
    }

    // Inserta una nueva semana al entrenamiento
    fun insertWeek(week: WeekModel) {
        viewModelScope.launch {
            getWeekUseCase.insertWeekToTraining(week)
        }
    }

    // Inserta una nueva rutina a una semana
    fun insertRoutine(routine: RoutineModel) {
        viewModelScope.launch {
            getRoutineUseCase.insertRoutineToWeek(routine)
        }
    }

    // Crea una copia de una semana según la opción seleccionada
    fun createCopyOfWeek(
        weekIdOriginal: Long?,
        trainingWeekId: Long,
        optionSelected: CopyOption?
    ) {
        viewModelScope.launch {
            getWeekUseCase.createCopyOfWeek(weekIdOriginal, trainingWeekId, optionSelected)
        }
    }

    // Elimina una rutina de la semana
    fun deleteRoutine(routine: RoutineModel) {
        viewModelScope.launch {
            getRoutineUseCase.deleteRoutine(routine)
        }
    }

    // Cambia el nombre de una rutina
    fun changeNameRoutine(routine: RoutineModel) {
        viewModelScope.launch {
            getRoutineUseCase.changeNameRoutine(routine)
        }
    }

    // Crea una copia de una rutina en la semana
    fun copyRoutine(routine: RoutineModel) {
        viewModelScope.launch {
            getRoutineUseCase.copyRoutine(routine, routine.weekRoutineId)
        }
    }

    // Asigna fechas a las rutinas o elimina fechas según la selección del usuario
    fun insertDatesToRoutines(
        routineList: List<RoutineModel>,
        removeDateList: List<LocalDate?>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val datesList = routineList.mapIndexedNotNull { index, routine ->
                    val dateString = routine.date
                    if (dateString != null) {
                        val date = getDateUseCase.getDate(dateString)
                        date?.copy(routineId = routine.routineId)
                            ?: DateEntity(dateString, null, null, routine.routineId)
                    } else {
                        // Si routine.date es null, usamos removeDateList en el mismo índice
                        val removedDate = removeDateList.getOrNull(index)?.toString()
                        if (removedDate != null) {
                            val date = getDateUseCase.getDate(removedDate)
                            date?.copy(routineId = null)
                                ?: DateEntity(removedDate, null, null, null)
                        } else {
                            // Si tampoco hay fecha en removeDateList, ignoramos este item
                            null
                        }
                    }
                }
                getDateUseCase.insertDateList(datesList)
            } catch (e: Exception) {
                Log.e("InsertDatesToRoutines", "Error al insertar dates a las rutinas", e)
            }
        }
    }

    // Cambia el orden de las rutinas dentro de una semana
    fun changeOrderRoutines(routines: List<RoutineModel>) {
        viewModelScope.launch {
            val newRoutinesList = routines.mapIndexed { index, routineModel ->
                routineModel.copy(order = index)
            }
            try {
                getRoutineUseCase.changeOrderRoutines(newRoutinesList)
            } catch (e: Exception) {
                Log.e("ChangeOrderRoutines", "Error al cambiar de orden las rutinas", e)
            }
        }
    }
}
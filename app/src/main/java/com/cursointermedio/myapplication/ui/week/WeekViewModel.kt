package com.cursointermedio.myapplication.ui.week

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.cursointermedio.myapplication.data.database.entities.DateEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.domain.useCase.CopyOption
import com.cursointermedio.myapplication.domain.useCase.GetDateUseCase
import com.cursointermedio.myapplication.domain.useCase.GetDetailsUseCase
import com.cursointermedio.myapplication.domain.useCase.GetExercisesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import com.cursointermedio.myapplication.ui.training.TrainingsUiState
import com.google.firebase.FirebaseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WeekViewModel @Inject constructor(
    private val getWeekUseCase: GetWeekUseCase,
    private val getRoutineUseCase: GetRoutineUseCase,
    private val getExercisesUseCase: GetExercisesUseCase,
    private val getDateUseCase: GetDateUseCase,
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {

    val trainingId: Long = savedStateHandle.get<Long>("id") ?: -1L

    private val _weeksWithRoutines = MutableStateFlow<WeekUiState>(WeekUiState.Loading)
    val weeksWithRoutines: StateFlow<WeekUiState> = _weeksWithRoutines

    private val _weeks = MutableStateFlow<List<WeekWithRoutinesModel>>(emptyList())
    val weeks: StateFlow<List<WeekWithRoutinesModel>> = _weeks

    private val _spinnerList = MutableStateFlow<List<String>>(emptyList())
    val spinnerList: StateFlow<List<String>> = _spinnerList

    private val _trainingName = MutableStateFlow("")
    val trainingName: StateFlow<String> = _trainingName

    init {
        viewModelScope.launch {
            getTrainingName()
        }

        @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
        viewModelScope.launch {
            getWeekUseCase.getAllWeeksWithRoutines(trainingId)
                .flatMapLatest { weeks ->
                    val weekFlows = weeks.map { week ->
                        val routineFlows = week.routineList.map { routine ->
                            combine(
                                getExercisesUseCase.getExerciseFromRoutineCount(routine.routineId!!),
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

    private fun getTrainingName() {
        viewModelScope.launch {
            _trainingName.value = getWeekUseCase.getTrainingName(trainingId) + " <>"
        }
    }

    fun insertWeek(week: WeekModel) {
        viewModelScope.launch {
            getWeekUseCase.insertWeekToTraining(week)
        }
    }

    fun insertRoutine(routine: RoutineModel) {
        viewModelScope.launch {
            getRoutineUseCase.insertRoutineToWeek(routine)
        }
    }

    fun createCopyOfWeek(
        weekIdOriginal: Long?,
        trainingWeekId: Long,
        optionSelected: CopyOption?
    ) {
        viewModelScope.launch {
            getWeekUseCase.createCopyOfWeek(weekIdOriginal, trainingWeekId, optionSelected)
        }
    }

    fun deleteRoutine(routine: RoutineModel) {
        viewModelScope.launch {
            getRoutineUseCase.deleteRoutine(routine)
        }
    }

    fun changeNameRoutine(routine: RoutineModel) {
        viewModelScope.launch {
            getRoutineUseCase.changeNameRoutine(routine)
        }
    }

    fun copyRoutine(routine: RoutineModel) {
        viewModelScope.launch {
            getRoutineUseCase.copyRoutine(routine, routine.weekRoutineId)
        }
    }

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

//    fun loadItems() {
//        if (isBack){
//            viewModelScope.launch {
//                getTrainingName()
//            }
//            viewModelScope.launch {
//                getWeekUseCase.getAllWeeksWithRoutines(trainingId)
//                    .flowOn(Dispatchers.IO)
//                    .catch { e -> _weeksWithRoutines.value = WeekUiState.Error(e.message ?: "Error") }
//                    .collectLatest { weeks ->
//                        _weeksWithRoutines.value = WeekUiState.Success(weeks).apply {
//                            Log.e("AAAA", "AAAAAAAAAAAA")
//                            _spinnerList.value = List(weeks.size) { index -> "Semana ${index + 1}" }
//                            _weeks.value = weeks
//                        }
//                    }
//            }
//        }
//
//    }


}
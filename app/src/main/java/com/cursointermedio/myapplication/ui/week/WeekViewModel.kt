package com.cursointermedio.myapplication.ui.week

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    private var isBack = false

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
        viewModelScope.launch {
            getWeekUseCase.getAllWeeksWithRoutines(trainingId)
                .flowOn(Dispatchers.IO)
                .catch { e -> _weeksWithRoutines.value = WeekUiState.Error(e.message ?: "Error") }
                .collectLatest { weeks ->
                    _weeksWithRoutines.value = WeekUiState.Success(weeks).apply {
                        _spinnerList.value = List(weeks.size) { index -> "Semana ${index + 1}" }
                        _weeks.value = weeks

                        weeks.map { it ->
                            it.routineList.map { routine ->
                                val numExercise = getExercisesUseCase.getExerciseFromRoutineCount(routine.routineId!!)
                                val date = getDateUseCase.getDatesFromRoutine(routine.routineId)
                                routine.exerciseCount = numExercise
                                routine.date = date
                            }
                        }
                    }
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
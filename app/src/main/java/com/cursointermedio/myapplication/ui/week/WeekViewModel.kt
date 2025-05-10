package com.cursointermedio.myapplication.ui.week

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.domain.useCase.CopyOption
import com.cursointermedio.myapplication.domain.useCase.GetDetailsUseCase
import com.cursointermedio.myapplication.domain.useCase.GetExercisesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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

    suspend fun insertRoutine(routine: RoutineModel) {
        getRoutineUseCase.insertRoutineToWeek(routine)
    }

    suspend fun createCopyOfWeek(weekIdOriginal: Long?, trainingWeekId: Long, optionSelected: CopyOption?) {
        getWeekUseCase.createCopyOfWeek(weekIdOriginal, trainingWeekId, optionSelected)
    }


}
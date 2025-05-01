package com.cursointermedio.myapplication.ui.week

import android.adservices.adid.AdId
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.domain.useCase.CopyOption
import com.cursointermedio.myapplication.domain.useCase.GetDetailsUseCase
import com.cursointermedio.myapplication.domain.useCase.GetExercisesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import com.cursointermedio.myapplication.domain.useCase.GetTrainingUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
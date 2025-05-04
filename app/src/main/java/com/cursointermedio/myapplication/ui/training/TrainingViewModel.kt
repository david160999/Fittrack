package com.cursointermedio.myapplication.ui.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.useCase.CopyOption
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import com.cursointermedio.myapplication.domain.useCase.GetTrainingUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import com.cursointermedio.myapplication.ui.training.CurrentFeature.*
import com.cursointermedio.myapplication.ui.training.CurrentFeature.TypeFeature.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val getTrainingUseCase: GetTrainingUseCase,
    private val getWeekUseCase: GetWeekUseCase,
) : ViewModel() {

    private val _trainings = MutableStateFlow<List<TrainingsWithWeekAndRoutineCounts>>(emptyList())
    val trainings: StateFlow<List<TrainingsWithWeekAndRoutineCounts>> = _trainings

    init {
        viewModelScope.launch {
            getTrainingUseCase.getTrainingsWithWeekAndRoutineCounts()
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    _trainings.value = it
                }
        }
    }

    fun getFeature(): Int? {
        val feature: Int? = when (getTypeFeature()) {
            TrainingFeature -> null
            WeekFeature -> R.id.action_trainingFragment_to_weekFragment
            RoutineFeature -> R.id.action_trainingFragment_to_routineFragment
            ExerciseFeature -> R.id.action_trainingFragment_to_exerciseFragment
        }
        return feature
    }

    private fun getTypeFeature(): TypeFeature = Feature.getTypeFeature()


    fun getTrainingsFromDataBase(): Flow<List<TrainingModel>> = getTrainingUseCase.invoke()


    suspend fun insertTraining(training: TrainingModel): Long {
        return getTrainingUseCase.insertTraining(training)
    }

    suspend fun insertWeek(week: WeekModel): Long {
        return getWeekUseCase.insertWeekToTraining(week)
    }

    suspend fun deleteAll() {
        getTrainingUseCase.deleteAll()
    }

    suspend fun deleteTraining(training: TrainingModel) {
        getTrainingUseCase.deleteTraining(training)
    }

    suspend fun copyTraining(training: TrainingsWithWeekAndRoutineCounts) {
        val trainingsWithWeekAndRoutine = getTrainingUseCase.getTrainingWithWeeksAndRoutines(training.training.trainingId!!)

        val oldTraining = training.training
        val newTraining = TrainingModel(null, oldTraining.name, null)

        val newTrainingId = getTrainingUseCase.insertTraining(newTraining)

        val oldWeeksAndRoutines = trainingsWithWeekAndRoutine.weekWithRoutinesList
        val oldWeekId = oldWeeksAndRoutines[oldWeeksAndRoutines.lastIndex].week.weekId

        getWeekUseCase.createCopyOfWeek(oldWeekId, newTrainingId, CopyOption.CopyAllDetails)

    }

    suspend fun changeNameTraining(training: TrainingModel) {
        getTrainingUseCase.changeNameTraining(training)
    }

    suspend fun shareTraining(training: TrainingModel) {
    }
}
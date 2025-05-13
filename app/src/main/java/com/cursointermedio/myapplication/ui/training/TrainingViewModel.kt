package com.cursointermedio.myapplication.ui.training

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.domain.exception.NetworkException
import com.cursointermedio.myapplication.domain.exception.TrainingNotFoundException
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.useCase.CopyOption
import com.cursointermedio.myapplication.domain.useCase.GetTrainingUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import com.cursointermedio.myapplication.ui.training.CurrentFeature.*
import com.cursointermedio.myapplication.ui.training.CurrentFeature.TypeFeature.*
import com.google.firebase.FirebaseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val getTrainingUseCase: GetTrainingUseCase,
    private val getWeekUseCase: GetWeekUseCase
) : ViewModel() {

    private val _trainingId = MutableLiveData<Long?>()
    val trainingId: LiveData<Long?> get() = _trainingId

    private val _trainingHashCode = MutableLiveData<Pair<String, String>?>()
    val trainingHashCode: LiveData<Pair<String, String>?> get() = _trainingHashCode

    private val _uiState = MutableStateFlow<TrainingsUiState>(TrainingsUiState.Loading)
    val uiState: StateFlow<TrainingsUiState> = _uiState

    private val _downloadState = MutableLiveData<Result<Unit>>() // Puede ser Result<Unit> si no necesitas retornar un dato
    val downloadState: LiveData<Result<Unit>> = _downloadState

    init {
        viewModelScope.launch {
            getTrainingUseCase.getTrainingsWithWeekAndRoutineCounts()
                .flowOn(Dispatchers.IO)
                .catch { e -> _uiState.value = TrainingsUiState.Error(e.message ?: "Error") }
                .collectLatest { trainings ->
                    _uiState.value = TrainingsUiState.Success(trainings)
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

    fun insertTrainingAndWeek(trainingName: String) {
        viewModelScope.launch {
            try {
                val training =
                    TrainingModel(trainingId = null, name = trainingName, description = null)
                val trainingId = getTrainingUseCase.insertTraining(training)

                val weekWithTraining = WeekModel(
                    weekId = null,
                    trainingWeekId = trainingId,
                    name = null,
                    description = null
                )

                getWeekUseCase.insertWeekToTraining(weekWithTraining)

                _trainingId.value = trainingId

            } catch (e: Exception) {
                _trainingId.value = null
            }
        }
    }

    suspend fun deleteAll() {
        getTrainingUseCase.deleteAll()
    }

    fun deleteTraining(training: TrainingModel) {
        viewModelScope.launch {
            getTrainingUseCase.deleteTraining(training)
        }
    }

    fun copyTraining(training: TrainingsWithWeekAndRoutineCounts) {
        viewModelScope.launch {
            val trainingsWithWeekAndRoutine =
                getTrainingUseCase.getTrainingWithWeeksAndRoutines(training.training.trainingId!!)

            val oldTraining = training.training
            val newTraining = TrainingModel(null, oldTraining.name, null)

            val newTrainingId = getTrainingUseCase.insertTraining(newTraining)

            val oldWeeksAndRoutines = trainingsWithWeekAndRoutine.weekWithRoutinesList
            val oldWeekId = oldWeeksAndRoutines[oldWeeksAndRoutines.lastIndex].week.weekId

            getWeekUseCase.createCopyOfWeek(oldWeekId, newTrainingId, CopyOption.CopyAllDetails)
        }

    }

    fun changeNameTraining(training: TrainingModel) {
        viewModelScope.launch {
            getTrainingUseCase.changeNameTraining(training)
        }
    }

    fun uploadTrainingData(training: TrainingModel) {
        val name = training.name

        viewModelScope.launch {
            try {
                val uniqueCode = getTrainingUseCase.uploadTrainingData(training)

                if (uniqueCode != null) {
                    _trainingHashCode.value = Pair(name, uniqueCode)
                } else {
                    _trainingHashCode.value = null
                }
            } catch (e: FirebaseException) {
                _trainingHashCode.value = null
            }
        }
    }

    fun downLoadTraining(code: String) {
        viewModelScope.launch {
            try {
                getTrainingUseCase.downLoadTrainingData(code)
                _downloadState.value = Result.success(Unit)
            } catch (e: TrainingNotFoundException) {
                _downloadState.value = Result.failure(e)
            } catch (e: NetworkException) {
                _downloadState.value = Result.failure(e)
            } catch (e: Exception){
                _downloadState.value = if (e.cause != null) {
                    Result.failure(e.cause!!)
                } else {
                    Result.failure(e)
                }            }
        }
    }
}

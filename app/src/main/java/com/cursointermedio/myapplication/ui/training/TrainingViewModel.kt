package com.cursointermedio.myapplication.ui.training

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.domain.mappers.TrainingMapper
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.useCase.CopyOption
import com.cursointermedio.myapplication.domain.useCase.GetDetailsUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import com.cursointermedio.myapplication.domain.useCase.GetTrainingUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import com.cursointermedio.myapplication.ui.training.CurrentFeature.*
import com.cursointermedio.myapplication.ui.training.CurrentFeature.TypeFeature.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val getTrainingUseCase: GetTrainingUseCase,
    private val getWeekUseCase: GetWeekUseCase
    ) : ViewModel() {

    private val _trainings = MutableStateFlow<List<TrainingsWithWeekAndRoutineCounts>>(emptyList())
    val trainings: StateFlow<List<TrainingsWithWeekAndRoutineCounts>> = _trainings

    private val _trainingId = MutableLiveData<Long?>()
    val trainingId: LiveData<Long?> get() = _trainingId

    private val _trainingHashCode = MutableLiveData<String?>()
    val trainingHashCode: LiveData<String?> get() = _trainingHashCode

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

    fun insertTrainingAndWeek(trainingName:String) {
        viewModelScope.launch {
            try {
                val training = TrainingModel(trainingId = null , name = trainingName, description= null)
                val trainingId = getTrainingUseCase.insertTraining(training)

                val weekWithTraining = WeekModel(weekId = null, trainingWeekId = trainingId, name = null, description = null )

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
        viewModelScope.launch {
            try {
                val uniqueCode = getTrainingUseCase.uploadTrainingData(training)

                if (uniqueCode != null) {
                    Log.d(
                        "Export",
                        "Datos exportados exitosamente con el código único: $uniqueCode"
                    )
                    _trainingHashCode.value = uniqueCode
                } else {
                    Log.w("Export", "Error al exportar datos")
                    _trainingHashCode.value = null
                }

            } catch (e: Exception) {
                _trainingHashCode.value = null
            }
        }
    }

    fun downLoadTraining(code: String) {
        viewModelScope.launch {
            getTrainingUseCase.downLoadTrainingData(code)
        }
    }
}

package com.cursointermedio.myapplication.ui.training

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val getTrainingUseCase: GetTrainingUseCase,
    private val getWeekUseCase: GetWeekUseCase
) : ViewModel() {

    // Emite el ID del entrenamiento recién creado, o null si falla la creación
    private val _trainingId = MutableSharedFlow<Long?>()
    val trainingId: SharedFlow<Long?> get() = _trainingId

    // Emite el par (nombre, código) al compartir entrenamiento
    private val _trainingHashCode = MutableSharedFlow<Pair<String, String>?>()
    val trainingHashCode: SharedFlow<Pair<String, String>?> get() = _trainingHashCode

    // Estado principal de la UI (loading, success, error)
    private val _uiState = MutableStateFlow<TrainingsUiState>(TrainingsUiState.Loading)
    val uiState: StateFlow<TrainingsUiState> = _uiState

    // Estado de la descarga de entrenamiento (éxito/error)
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

    // Devuelve el ID de navegación según la feature seleccionada (para Deep Linking o navegación modular)
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

    // Devuelve todos los entrenamientos como flujo (para otros usos)
    fun getTrainingsFromDataBase(): Flow<List<TrainingModel>> = getTrainingUseCase.invoke()

    // Crea un nuevo entrenamiento y una semana asociada, y emite el ID creado
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

                _trainingId.emit(trainingId)

            } catch (e: Exception) {
                _trainingId.emit(null)
            }
        }
    }

    suspend fun deleteAll() {
        getTrainingUseCase.deleteAll()
    }

    // Elimina un entrenamiento
    fun deleteTraining(training: TrainingModel) {
        viewModelScope.launch {
            getTrainingUseCase.deleteTraining(training)
        }
    }

    // Copia un entrenamiento con todas sus semanas y rutinas
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

    // Cambia el nombre de un entrenamiento
    fun changeNameTraining(training: TrainingModel) {
        viewModelScope.launch {
            getTrainingUseCase.changeNameTraining(training)
        }
    }

    // Sube un entrenamiento a la nube y emite su código si tiene éxito
    fun uploadTrainingData(training: TrainingModel) {
        val name = training.name

        viewModelScope.launch {
            try {
                val uniqueCode = getTrainingUseCase.uploadTrainingData(training)

                if (uniqueCode != null) {
                    _trainingHashCode.emit(Pair(name, uniqueCode))
                } else {
                    _trainingHashCode.emit(null)
                }
            } catch (e: FirebaseException) {
                _trainingHashCode.emit(null)
            }
        }
    }

    // Alternativa: obtener flujo de código generado al subir
    fun uploadTrainingDataFlow(training: TrainingModel): Flow<Pair<String, String>?> = flow {
        val name = training.name
        viewModelScope.launch {
            try {
                val uniqueCode = getTrainingUseCase.uploadTrainingData(training)

                if (uniqueCode != null) {
                    emit(Pair(name, uniqueCode))
                } else {
                    emit(null)
                }
            } catch (e: FirebaseException) {
                emit(null)
            }
        }
    }

    // Descarga un entrenamiento de la nube usando un código y actualiza el estado
    fun downLoadTraining(code: String) {
        viewModelScope.launch {
            try {
                getTrainingUseCase.downLoadTrainingData(code)
                _downloadState.value = Result.success(Unit)
            } catch (e: TrainingNotFoundException) {
                _downloadState.value = Result.failure(e)
            } catch (e: NetworkException) {
                _downloadState.value = Result.failure(e)
            } catch (e: Exception) {
                _downloadState.value = if (e.cause != null) {
                    Result.failure(e.cause!!)
                } else {
                    Result.failure(e)
                }
            }
        }
    }
}
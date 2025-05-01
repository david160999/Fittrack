package com.cursointermedio.myapplication.ui.training

import androidx.lifecycle.ViewModel
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val getTrainingUseCase: GetTrainingUseCase,
    private val getWeekUseCase: GetWeekUseCase,
    private val getRoutineUseCase: GetRoutineUseCase
) : ViewModel() {


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

    fun getTrainingWithWeeksAndRoutines(): Flow<List<TrainingWithWeeksAndRoutines>> =
        getTrainingUseCase.getTrainingWithWeeksAndRoutines()

    suspend fun deleteAll() {
        getTrainingUseCase.deleteAll()
    }

    suspend fun deleteTraining(training: TrainingModel) {
        getTrainingUseCase.deleteTraining(training)
    }

    suspend fun copyTraining(training: TrainingWithWeeksAndRoutines) {
        val oldTraining = training.training
        val newTraining = TrainingModel(null, oldTraining.name, null)

        val newTrainingId = getTrainingUseCase.insertTraining(newTraining)

        val oldWeeksAndRoutines = training.weekWithRoutinesList
        val oldWeekId = oldWeeksAndRoutines[oldWeeksAndRoutines.lastIndex].week.weekId

        getWeekUseCase.createCopyOfWeek(oldWeekId, newTrainingId, CopyOption.CopyAllDetails)

    }

    suspend fun changeNameTraining(training: TrainingModel) {

    }

    suspend fun shareTraining(training: TrainingModel) {
    }
}
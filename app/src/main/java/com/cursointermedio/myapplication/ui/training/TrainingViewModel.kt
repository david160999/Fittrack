package com.cursointermedio.myapplication.ui.training

import androidx.lifecycle.ViewModel
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.useCase.GetTrainingUseCase
import com.cursointermedio.myapplication.ui.training.CurrentFeature.*
import com.cursointermedio.myapplication.ui.training.CurrentFeature.TypeFeature.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val getTrainingUseCase: GetTrainingUseCase
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





    suspend fun insertTraining(training : TrainingModel){

        getTrainingUseCase.invoke2(training)
    }

}
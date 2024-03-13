package com.cursointermedio.myapplication.ui.training

import androidx.lifecycle.ViewModel
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.ui.training.CurrentFeature.*
import com.cursointermedio.myapplication.ui.training.CurrentFeature.TypeFeature.*


class TrainingViewModel : ViewModel() {


    fun getFeature(): Int? {
        val feature: Int? = when (getTypeFeature()) {
            TrainingFeature -> null
            WeekFeature -> R.id.action_trainingFragment_to_weekFragment
            RoutineFeature -> R.id.action_trainingFragment_to_routineFragment
            ExerciseFeature -> R.id.action_trainingFragment_to_exerciseFragment
        }
        return feature
    }

    private fun getTypeFeature(): TypeFeature {
       return Feature.getTypeFeature()
    }
}
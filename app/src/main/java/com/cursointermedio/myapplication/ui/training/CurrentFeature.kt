package com.cursointermedio.myapplication.ui.training

class CurrentFeature {

    enum class TypeFeature {
        TrainingFeature, WeekFeature, RoutineFeature, ExerciseFeature
    }

    object Feature {
        private var currentFeature: TypeFeature = TypeFeature.TrainingFeature

        fun setFeature(feature: TypeFeature) {
            currentFeature = feature
        }

        fun getTypeFeature() = currentFeature
    }
}
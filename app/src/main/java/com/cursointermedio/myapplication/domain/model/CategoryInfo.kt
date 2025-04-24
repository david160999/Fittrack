package com.cursointermedio.myapplication.domain.model

import androidx.core.content.ContextCompat
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.CategoryEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import kotlin.coroutines.coroutineContext

sealed class CategoryInfo(val name: Int, val id: Long) {
    data object ExerciseKnee : CategoryInfo(R.string.exerciseKnee, 1)
    data object ExerciseHip : CategoryInfo(R.string.exerciseHip, 2)
    data object ExercisePushHorizontal : CategoryInfo(R.string.exercisePushHorizontal, 3)
    data object ExercisePushVertical : CategoryInfo(R.string.exercisePushVertical, 4)
    data object ExercisePullHorizontal : CategoryInfo(R.string.exercisePullHorizontal, 5)
    data object ExercisePullVertical : CategoryInfo(R.string.exercisePullVertical, 6)
    data object Exerciseweightlifting : CategoryInfo(R.string.exerciseweightlifting, 7)
    data object ExerciseOthers : CategoryInfo(R.string.exerciseOthers, 8)
    companion object {
        fun getAll(): List<CategoryInfo> = listOf(
            ExerciseKnee,
            ExerciseHip,
            ExercisePushHorizontal,
            ExercisePushVertical,
            ExercisePullHorizontal,
            ExercisePullVertical,
            Exerciseweightlifting,
            ExerciseOthers
        )
        fun fromId(id: Long): CategoryInfo = getAll().find { it.id == id } ?: ExerciseOthers
    }

}
fun CategoryEntity.toDomain(): CategoryInfo = CategoryInfo.fromId(categoryId)

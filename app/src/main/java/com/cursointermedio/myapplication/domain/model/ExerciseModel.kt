package com.cursointermedio.myapplication.domain.model

import android.annotation.SuppressLint
import android.content.Context
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity

data class ExerciseModel(
    val id: Long?,
    val key: String?,
    val categoryExerciseId:Long?,
    val name: String?,
)
@SuppressLint("DiscouragedApi")
fun ExerciseModel.getExerciseNameFromKey(context: Context): String? {
    val resId = context.resources.getIdentifier(key, "string", context.packageName)
    return if (resId != 0) context.getString(resId) else key // fallback si no encuentra
}
fun ExerciseModel.getExerciseDescriptionFromKey(context: Context): String? {
    val resId = context.resources.getIdentifier(key, "string", context.packageName)
    return if (resId != 0) context.getString(resId) else key // fallback si no encuentra
}

fun ExerciseEntity.toDomain() = ExerciseModel(exerciseId, key, categoryExerciseId, name)


data class RealSeriesModel(
    val id: Long?,
    val weight: Int?,
    val reps: Int?,
    val rpe: Int?,
    val objective: Int?
)

data class ObjectiveSeriesModel(
    val id: Long?,
    val weight: Int?,
    val reps: Int?,
    val rpe: Int?,
    val objective: Int?
)
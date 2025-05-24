package com.cursointermedio.myapplication.domain.model

import android.annotation.SuppressLint
import android.content.Context
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity

data class ExerciseModel(
    val id: Long?,
    val key: String?,
    val categoryExerciseId: Long?,
    val name: String?,
    val detailCount: Int? = 0
)

@SuppressLint("DiscouragedApi")
fun ExerciseModel.getExerciseNameFromKey(context: Context): String {
    return name ?: run {
        val resId = context.resources.getIdentifier(key, "string", context.packageName)
        if (resId != 0) context.getString(resId) else key.orEmpty()
    }
}

fun ExerciseModel.getExerciseDescriptionFromKey(context: Context): String? {
    val resources = context.resources
    val packageName = context.packageName

    val descriptionKey = "${key}_description"
    val descriptionResId = resources.getIdentifier(descriptionKey, "string", packageName)

    return when {
        descriptionResId != 0 -> resources.getString(descriptionResId)
        resources.getIdentifier(key, "string", packageName) != 0 ->
            resources.getString(resources.getIdentifier(key, "string", packageName))

        else -> context.getString(R.string.description_not_available)
    }
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
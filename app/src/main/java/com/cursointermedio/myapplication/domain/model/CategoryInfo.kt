package com.cursointermedio.myapplication.domain.model

import androidx.core.content.ContextCompat
import com.cursointermedio.myapplication.R
import kotlin.coroutines.coroutineContext

sealed class CategoryInfo(val name: Int) {
    data object exerciseKnee : CategoryInfo(R.string.exerciseKnee)
    data object exerciseHip : CategoryInfo(R.string.exerciseHip)
    data object exercisePushHorizontal : CategoryInfo(R.string.exercisePushHorizontal)
    data object exercisePushVertical : CategoryInfo(R.string.exercisePushVertical)
    data object exercisePullHorizontal : CategoryInfo(R.string.exercisePullHorizontal)
    data object exercisePullVertical : CategoryInfo(R.string.exercisePullVertical)
    data object exerciseweightlifting : CategoryInfo(R.string.exerciseweightlifting)
    data object exerciseOthers : CategoryInfo(R.string.exerciseOthers)

}
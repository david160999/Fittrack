package com.cursointermedio.myapplication.ui.training.adapter

import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.domain.model.TrainingModel

data class TrainingMenuActions(
    val onChangeName: (TrainingModel) -> Unit,
    val onCopy: (TrainingsWithWeekAndRoutineCounts) -> Unit,
    val onShare: (TrainingModel) -> Unit,
    val onEliminate: (TrainingModel) -> Unit
)
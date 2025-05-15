package com.cursointermedio.myapplication.ui.routine.adapter

import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.TrainingModel


data class RoutineMenuActions(
    val onChangeName: (RoutineModel) -> Unit,
    val onCopy: (RoutineModel) -> Unit,
    val onMove: ((RoutineModel) -> Unit)? = null,
    val onEliminate: (RoutineModel) -> Unit,
)
package com.cursointermedio.myapplication.domain.model

import androidx.room.ColumnInfo
import com.cursointermedio.myapplication.data.database.entities.DetailsEntity
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines

data class DetailModel(
    val detailsId: Long?,
    val routineDetailsId: Long,
    val exerciseDetailsId: Long,
    val realWeight: Int?,
    val realReps: Int?,
    val realRpe: Int?,
    val objWeight: Int?,
    val objReps: Int?,
    val objRpe: Int?
)
fun DetailsEntity.toDomain() = DetailModel(detailsId, routineDetailsId, exerciseDetailsId, realWeight, realReps, realRpe, objWeight, objReps, objRpe)

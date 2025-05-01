package com.cursointermedio.myapplication.domain.useCase

import com.cursointermedio.myapplication.data.database.entities.DetailsEntity
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.data.repository.DetailsRepository
import com.cursointermedio.myapplication.data.repository.ExerciseRepository
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDetailsUseCase @Inject constructor(
    private val repository: DetailsRepository
) {

    fun getDetailOfRoutine(routineId: Long): List<DetailModel> {
        return repository.getDetailOfRoutine(routineId)
    }

    suspend fun insertDetailToRoutineExercise(detail: DetailModel){
        repository.insertDetailToRoutineExercise(detail.toDatabase())
    }

    suspend fun copyDetailsToNewWeek(routineId: Long, newRoutineId: Long) {
        val detalles = getDetailOfRoutine(routineId)

        detalles.forEach() { detalle ->
            val nuevoDetalle = DetailModel(
                detailsId = 0,
                routineDetailsId = newRoutineId,
                exerciseDetailsId = detalle.exerciseDetailsId,
                realWeight = detalle.realWeight,
                realReps = detalle.realReps,
                realRpe = detalle.realRpe,
                objWeight = detalle.objWeight,
                objReps = detalle.objReps,
                objRpe = detalle.objRpe
            )
            insertDetailToRoutineExercise(nuevoDetalle)
        }

    }

    suspend fun copyOnlyObjectiveToNewWeek(routineId: Long, newRoutineId: Long) {
        val detalles = getDetailOfRoutine(routineId)

        detalles.forEach() { detalle ->
            val nuevoDetalle = DetailModel(
                detailsId = 0,
                routineDetailsId = newRoutineId,
                exerciseDetailsId = detalle.exerciseDetailsId,
                realWeight = null,
                realReps = null,
                realRpe = null,
                objWeight = detalle.objWeight,
                objReps = detalle.objReps,
                objRpe = detalle.objRpe
            )
            insertDetailToRoutineExercise(nuevoDetalle)
        }

    }
}
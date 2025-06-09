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
    // Devuelve un Flow con la lista de detalles para una rutina y ejercicio específicos.
    fun getDetailOfRoutineAndExerciseFlow(routineId: Long, exerciseId: Long): Flow<List<DetailModel>> {
        return repository.getDetailOfRoutineAndExerciseFlow(routineId, exerciseId)
    }

    // Inserta un detalle de rutina convertido a entidad en la base de datos.
    suspend fun insertDetailToRoutineExercise(detail: DetailModel) {
        repository.insertDetailToRoutineExercise(detail.toDatabase())
    }

    // Actualiza una lista de detalles de rutina convirtiéndolos a entidades antes de guardar.
    suspend fun updateDetailToRoutineExercise(detail: List<DetailModel>) {
        repository.updateDetailToRoutineExercise(detail.map { it.toDatabase() })
    }

    // Copia todos los detalles de una rutina a una nueva rutina, asignando un nuevo ID y manteniendo todos los valores.
    suspend fun copyDetailsToNewWeek(routineId: Long, newRoutineId: Long) {
        val detalles = repository.getDetailOfRoutine(routineId)

        detalles.forEach { detalle ->
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

    // Copia solo los valores objetivos (objetivos) de una rutina a una nueva rutina, dejando nulos los valores reales.
    suspend fun copyOnlyObjectiveToNewWeek(routineId: Long, newRoutineId: Long) {
        val detalles = repository.getDetailOfRoutine(routineId)

        detalles.forEach { detalle ->
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

    // Elimina el último detalle registrado para una combinación de rutina y ejercicio.
    suspend fun deleteLastDetail(routineId: Long, exerciseId: Long) {
        repository.deleteLastDetail(routineId = routineId, exerciseId = exerciseId)
    }

}
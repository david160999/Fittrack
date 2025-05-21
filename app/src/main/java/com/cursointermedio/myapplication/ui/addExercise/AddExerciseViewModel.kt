package com.cursointermedio.myapplication.ui.addExercise

import androidx.lifecycle.ViewModel
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.RoutineWithExercises
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.useCase.GetExercisesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class AddExerciseViewModel @Inject constructor(
    private val getExercisesUseCase: GetExercisesUseCase,

) : ViewModel() {


    fun getAllExercise(): Flow<List<ExerciseModel>> {
        return getExercisesUseCase.invoke()
    }

    suspend fun getExercisesFromCategory(categoryId: Long): List<ExerciseModel> {
        return getExercisesUseCase.getExercisesFromCategory(categoryId)
    }

    suspend fun getCategories(): List<CategoryInfo> = getExercisesUseCase.getCategories()

    suspend fun insertExerciseToRoutine(
        routineId: Long,
        selectedExercises: List<ExerciseModel>
    ) {
        selectedExercises.forEachIndexed { index, exercise ->
            val crossRef = RoutineExerciseCrossRef(
                routineId = routineId,
                exerciseId = exercise.id!!,
                order = index
            )
            getExercisesUseCase.insertExerciseToRoutine(crossRef)
        }
    }
    suspend fun insertExercise(exercise: ExerciseModel) {
        getExercisesUseCase.insertExercise(exercise)
    }
}
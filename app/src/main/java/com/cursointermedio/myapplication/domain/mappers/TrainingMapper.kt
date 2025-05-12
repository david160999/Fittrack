package com.cursointermedio.myapplication.domain.mappers

import android.util.Log
import androidx.recyclerview.widget.SnapHelper
import com.cursointermedio.myapplication.data.database.entities.DetailsEntity
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.RoutineWithExercises
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.data.repository.DetailsRepository
import com.cursointermedio.myapplication.data.repository.ExerciseRepository
import com.cursointermedio.myapplication.data.repository.RoutineRepository
import com.cursointermedio.myapplication.data.repository.TrainingRepository
import com.cursointermedio.myapplication.data.repository.UserRepositoryImpl
import com.cursointermedio.myapplication.data.repository.WeekRepository
import com.cursointermedio.myapplication.domain.model.TrainingFirebaseModel
import com.cursointermedio.myapplication.domain.useCase.GetDetailsUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import com.cursointermedio.myapplication.domain.useCase.GetTrainingUseCase
import com.google.firebase.firestore.DocumentSnapshot
import okhttp3.internal.cache.DiskLruCache.Snapshot
import javax.inject.Inject

class TrainingMapper @Inject constructor(
    private val trainingRepository: TrainingRepository,
    private val routineRepository: RoutineRepository,
    private val detailsRepository: DetailsRepository,
    private val weekRepository: WeekRepository,
    private val exerciseRepository: ExerciseRepository,
    private val userRepositoryImpl: UserRepositoryImpl
) {

    suspend operator fun invoke(trainingId: Long): Map<String, Any?> {
        val fullTraining = trainingRepository.getTrainingWithWeeksAndRoutines(trainingId)
        return buildFirebaseMap(fullTraining)
    }


    private suspend fun buildFirebaseMap(trainingFull: TrainingWithWeeksAndRoutines): Map<String, Any?> {
        val training = trainingFull.training
        val weeks = trainingFull.weekWithRoutinesList
        val user = userRepositoryImpl.getUserData()

//      Entrenamiento
        return mapOf(
            "trainingId" to training.trainingId,
            "name" to training.name,
            "description" to training.description,
            "author_uid" to user.id,
            "weeks" to listOfNotNull(weeks.lastOrNull()).map { week ->
//                      Semanas
                hashMapOf(
                    "weekId" to week.week.weekId,
                    "name" to week.week.name,
                    "description" to week.week.description,
                    "routines" to week.routineList.map { routine ->
//                              Rutinas
                        val routineWithExercise =
                            routineRepository.getRoutineWithExercises(routine.routineId!!)
                        hashMapOf(
                            "routineId" to routine.routineId,
                            "name" to routine.name,
                            "description" to routine.description,
                            "exercises" to routineWithExercise.exercises.map { exercise ->
//                                      Ejercicios
                                val details = detailsRepository.getDetailOfRoutineAndExercise(
                                    routineId = routine.routineId,
                                    exerciseID = exercise.exerciseId!!
                                )
                                hashMapOf(
                                    "exerciseId" to exercise.exerciseId,
                                    "key" to exercise.key,
                                    "categoryExerciseId" to exercise.categoryExerciseId,
                                    "name" to exercise.name,
                                    "details" to details.map { detail ->
//                                              Detalles
                                        hashMapOf(
                                            "detailsId" to detail.detailsId,
                                            "realWeight" to detail.realWeight,
                                            "realReps" to detail.realReps,
                                            "realRpe" to detail.realRpe,
                                            "objWeight" to detail.objWeight,
                                            "objReps" to detail.objReps,
                                            "objRpe" to detail.objRpe
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
            }
        )
    }

    suspend fun fromFirebaseToLocalDatabase(trainingFull: DocumentSnapshot?) {
        try {
            if (trainingFull != null) {
                val trainingFirebaseModel =
                    trainingFull.toObject(TrainingFirebaseModel::class.java)!!

                val newTraining =
                    TrainingEntity(
                        null,
                        trainingFirebaseModel.name,
                        trainingFirebaseModel.description
                    )
                val newTrainingId = trainingRepository.insertTraining(newTraining)

                trainingFirebaseModel.weeks.map { week ->
                    val newWeek = WeekEntity(null, newTrainingId, week.name, week.description)
                    val newWeekId = weekRepository.insertWeekToTraining(newWeek)

                    week.routines.map { routine ->
                        val newRoutine =
                            RoutineEntity(null, newWeekId, routine.name, routine.description)
                        val newRoutineId = routineRepository.insertRoutineToWeek(newRoutine)

                        routine.exercises.map { exercise ->
                            val newExercise = ExerciseEntity(
                                exercise.exerciseId,
                                exercise.key,
                                exercise.categoryExerciseId,
                                exercise.name
                            )
                            exerciseRepository.insertExercise(newExercise)

                            val crossReference = RoutineExerciseCrossRef(
                                routineId = routine.routineId!!,
                                exerciseId = exercise.exerciseId!!
                            )
                            exerciseRepository.insertExerciseToRoutine(crossReference)

                            exercise.details.map { detail ->
                                val newDetail = DetailsEntity(
                                    null,
                                    routineDetailsId = newRoutineId,
                                    exerciseDetailsId = exercise.exerciseId,
                                    realWeight = detail.realWeight,
                                    realReps = detail.realReps,
                                    realRpe = detail.realRpe,
                                    objRpe = detail.objRpe,
                                    objReps = detail.objReps,
                                    objWeight = detail.objWeight
                                )
                                detailsRepository.insertDetailToRoutineExercise(newDetail)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Error", "Error al intentar insertar los datos en la base de datos, ${e.message}")

        }

    }

}
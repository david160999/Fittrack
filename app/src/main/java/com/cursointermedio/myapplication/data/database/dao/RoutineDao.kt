package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.RoutineWithExercises
import com.cursointermedio.myapplication.data.database.entities.RoutineWithOrderedExercises
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Dao
interface RoutineDao {

    // Obtiene las rutinas asociadas a una semana específica como Flow reactivo.
    @Query("SELECT * FROM routine_table WHERE weekRoutineId = :weekID")
    fun getRoutinesWeek(weekID: Long): Flow<MutableList<RoutineEntity>>

    // Inserta o reemplaza una rutina, devuelve el id generado (si es autogenerado).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineToWeek(routine: RoutineEntity): Long

    // Obtiene una rutina junto con sus ejercicios (relación definida por @Transaction).
    @Transaction
    @Query("SELECT * FROM routine_table WHERE routineId = :routineId")
    suspend fun getRoutineWithExercises(routineId: Long): RoutineWithExercises

    // Inserta o reemplaza la relación entre rutina y ejercicio.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExerciseCrossRef(crossRef: RoutineExerciseCrossRef)

    // Actualiza una rutina (ejemplo: cambiar nombre).
    @Update
    suspend fun changeNameRoutine(routine: RoutineEntity)

    // Elimina una rutina.
    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    // Actualiza el orden de una lista de rutinas.
    @Update
    suspend fun changeOrderRoutines(routines: List<RoutineEntity>)

    // Obtiene una rutina puntual por ID (suspend).
    @Query("SELECT * FROM routine_table WHERE routineId = :routineId")
    suspend fun getRoutineById(routineId: Long): RoutineEntity

    // Obtiene una rutina puntual por ID como Flow reactivo.
    @Query("SELECT * FROM routine_table WHERE routineId = :routineId")
    fun getRoutineByIdFlow(routineId: Long): Flow<RoutineEntity>

    // Obtiene los ejercicios de una rutina ordenados por el campo `order` en la tabla cruzada.
    @Query("""
        SELECT e.* FROM exercise_table e
        INNER JOIN RoutineExerciseCrossRef re ON e.exerciseId = re.exerciseId
        WHERE re.routineId = :routineId
        ORDER BY re.`order` ASC
    """)
    suspend fun getOrderedExercisesForRoutine(routineId: Long): List<ExerciseEntity>

    // Igual que la anterior pero con Flow para observar cambios en tiempo real.
    @Query("""
        SELECT e.* FROM exercise_table e
        INNER JOIN RoutineExerciseCrossRef re ON e.exerciseId = re.exerciseId
        WHERE re.routineId = :routineId
        ORDER BY re.`order` ASC
    """)
    fun getOrderedExercisesForRoutineFlow(routineId: Long): Flow<List<ExerciseEntity>>

    // Función para obtener una rutina con sus ejercicios ordenados (combina dos consultas suspend).
    @Transaction
    suspend fun getRoutineWithOrderedExercises(routineId: Long): RoutineWithOrderedExercises {
        val routine = getRoutineById(routineId)
        val exercises = getOrderedExercisesForRoutine(routineId)
        return RoutineWithOrderedExercises(routine, exercises)
    }

    // Igual que la anterior pero devuelve Flow combinando dos Flows.
    fun getRoutineWithOrderedExercisesFlow(routineId: Long): Flow<RoutineWithOrderedExercises> =
        combine(
            getRoutineByIdFlow(routineId),
            getOrderedExercisesForRoutineFlow(routineId)
        ) { routine, exercises ->
            RoutineWithOrderedExercises(routine, exercises)
        }

    // Elimina una relación ejercicio-rutina.
    @Delete
    suspend fun removeExerciseFromRoutine(crossRef: RoutineExerciseCrossRef)

    // Actualiza el campo `order` en la tabla cruzada para reordenar ejercicios dentro de una rutina.
    @Query("UPDATE RoutineExerciseCrossRef SET `order` = :order WHERE routineId = :routineId AND exerciseId = :exerciseId")
    suspend fun updateOrderCrossRefRoutineExercise(exerciseId: Long, routineId: Long, order: Int)
}
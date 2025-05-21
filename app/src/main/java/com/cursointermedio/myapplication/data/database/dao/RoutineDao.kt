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
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {


    @Query("SELECT * FROM routine_table WHERE :weekID = weekRoutineId")
    fun getRoutinesWeek(weekID: Long): Flow<MutableList<RoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineToWeek(routine: RoutineEntity): Long

    @Transaction
    @Query("SELECT * FROM routine_table WHERE routineId = :routineId")
    suspend fun getRoutineWithExercises(routineId: Long): RoutineWithExercises

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExerciseCrossRef(crossRef: RoutineExerciseCrossRef)

    @Update
    suspend fun changeNameRoutine(routine: RoutineEntity)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Update
    suspend fun changeOrderRoutines(routines: List<RoutineEntity>)

    @Query("SELECT * FROM routine_table WHERE routineId = :routineId")
    suspend fun getRoutineById(routineId: Long): RoutineEntity

    @Query("""
    SELECT e.* FROM exercise_table e
    INNER JOIN RoutineExerciseCrossRef re ON e.exerciseId = re.exerciseId
    WHERE re.routineId = :routineId
    ORDER BY re.`order` ASC
""")
    suspend fun getOrderedExercisesForRoutine(routineId: Long): List<ExerciseEntity>

    @Transaction
    suspend fun getRoutineWithOrderedExercises(routineId: Long): RoutineWithOrderedExercises {
        val routine = getRoutineById(routineId)
        val exercises = getOrderedExercisesForRoutine(routineId)
        return RoutineWithOrderedExercises(routine, exercises)
    }
}
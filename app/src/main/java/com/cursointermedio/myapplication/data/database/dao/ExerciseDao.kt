package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cursointermedio.myapplication.data.database.entities.CategoryEntity
import com.cursointermedio.myapplication.data.database.entities.ExerciseDetailsCount
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    // Obtiene todos los ejercicios como Flow para observar cambios en tiempo real.
    @Query("SELECT * FROM exercise_table")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    // Obtiene ejercicios relacionados a una rutina específica.
    @Query("SELECT * FROM exercise_table WHERE exerciseId = :routineID")
    fun getExercisesRoutine(routineID: Int): Flow<MutableList<ExerciseEntity>>

    // Inserta un ejercicio, ignora si ya existe (para evitar duplicados).
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    // Inserta o reemplaza la relación entre ejercicio y rutina.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseToRoutine(exercise: RoutineExerciseCrossRef)

    // Obtiene los ejercicios que pertenecen a una categoría específica.
    @Query("SELECT * FROM exercise_table WHERE categoryExerciseId = :categoryId")
    suspend fun getExercisesFromCategory(categoryId: Long): List<ExerciseEntity>

    // Obtiene todas las categorías (no es típico que esté en ExerciseDao, pero puede estar).
    @Query("SELECT * FROM category_table")
    suspend fun getCategories(): List<CategoryEntity>

    // Cuenta los ejercicios de una rutina, devuelve un Flow para observar cambios.
    @Query("SELECT COUNT(*) FROM RoutineExerciseCrossRef WHERE routineId = :routineId")
    fun getExerciseFromRoutineCountFlow(routineId: Long): Flow<Int>

    // Cuenta los ejercicios de una rutina, llamado puntual suspend.
    @Query("SELECT COUNT(*) FROM RoutineExerciseCrossRef WHERE routineId = :routineId")
    suspend fun getExerciseFromRoutineCount(routineId: Long): Int

    // Cuenta los detalles de un ejercicio en una rutina.
    @Query("""
        SELECT COUNT(*) 
        FROM details_table 
        WHERE exerciseDetailsId = :exerciseId 
        AND routineDetailsId = :routineId
    """)
    suspend fun getDetailCountFromExercise(exerciseId: Long, routineId: Long): Int

    // Obtiene las notas asociadas a la relación ejercicio-rutina.
    @Query("SELECT notes FROM routineexercisecrossref WHERE exerciseId = :exerciseId AND routineId = :routineId")
    suspend fun getNotesFromCrossRef(routineId: Long, exerciseId: Long): String?

    // Actualiza las notas asociadas a la relación ejercicio-rutina.
    @Query("UPDATE routineexercisecrossref SET notes = :notes WHERE exerciseId = :exerciseId AND routineId = :routineId")
    suspend fun updateNotesFromCrossRef(routineId: Long, exerciseId: Long, notes: String)

    // Obtiene un ejercicio específico por su ID.
    @Query("SELECT * FROM exercise_table WHERE exerciseId = :exerciseId")
    suspend fun getExercise(exerciseId: Long): ExerciseEntity
}
package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    // Obtiene todos los entrenamientos como un Flow reactivo que actualiza la UI cuando cambian.
    @Query("SELECT * FROM training_table")
    fun getAllTraining(): Flow<List<TrainingEntity>>

    // Inserta un entrenamiento o reemplaza el existente en caso de conflicto.
    // Devuelve el id del entrenamiento insertado (útil si es autogenerado).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraining(training: TrainingEntity): Long

    // Elimina un entrenamiento específico.
    @Delete
    suspend fun deleteTraining(training: TrainingEntity)

    // Elimina todos los entrenamientos de la tabla.
    @Query("DELETE FROM training_table")
    suspend fun deleteAllTraining()

    // Obtiene un entrenamiento con sus semanas y rutinas relacionadas (relaciones definidas con @Transaction).
    @Transaction
    @Query("SELECT * FROM training_table WHERE trainingId = :trainingId")
    suspend fun getTrainingWithWeeksAndRoutines(trainingId: Long): TrainingWithWeeksAndRoutines

    /**
     * Obtiene una lista de entrenamientos con el conteo de semanas y rutinas asociadas.
     *
     * Explicación de la consulta:
     * - Cuenta el número de semanas distintas asociadas a cada entrenamiento.
     * - Para cada entrenamiento, obtiene el número de rutinas de la última semana asociada.
     * - Usa LEFT JOIN para incluir entrenamientos sin semanas.
     * - Agrupa por entrenamiento y ordena por id descendente (los más recientes primero).
     */
    @Transaction
    @Query(
        """
        SELECT 
            t.trainingId,
            t.name,
            t.description,
            COUNT(DISTINCT w.weekId) AS numWeeks,
            (
                SELECT COUNT(*) FROM routine_table r
                WHERE r.weekRoutineId = (
                    SELECT w2.weekId FROM week_table w2
                    WHERE w2.trainingWeekId = t.trainingId
                    ORDER BY w2.weekId DESC
                    LIMIT 1
                )
            ) AS numRoutines
        FROM training_table t
        LEFT JOIN week_table w ON t.trainingId = w.trainingWeekId
        GROUP BY t.trainingId
        ORDER BY t.trainingId DESC
        """
    )
    fun getTrainingsWithWeekAndRoutineCounts(): Flow<List<TrainingsWithWeekAndRoutineCounts>>

    // Actualiza el entrenamiento, por ejemplo para cambiar el nombre o la descripción.
    @Update
    suspend fun changeNameTraining(training: TrainingEntity)
}


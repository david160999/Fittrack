package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cursointermedio.myapplication.data.database.entities.DetailsEntity
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.domain.model.DetailModel
import kotlinx.coroutines.flow.Flow

@Dao
interface DetailsDao {

    /**
     * Obtiene los detalles de un ejercicio específico dentro de una rutina como Flow reactivo.
     */
    @Query("SELECT * FROM details_table WHERE routineDetailsId = :routineID AND exerciseDetailsId = :exerciseID")
    fun getDetailOfRoutineExercise(
        routineID: Long,
        exerciseID: Long
    ): Flow<MutableList<DetailsEntity>>

    /**
     * Obtiene la lista de detalles asociados a una rutina específica.
     */
    @Query("SELECT * FROM details_table WHERE routineDetailsId = :routineID")
    fun getDetailOfRoutine(routineID: Long): List<DetailsEntity>

    /**
     * Inserta o reemplaza un detalle para un ejercicio en una rutina.
     */
    @Insert
    suspend fun insertDetailToRoutineExercise(details: DetailsEntity)

    /**
     * Actualiza una lista de detalles (varios registros) en la base de datos.
     */
    @Update
    suspend fun updateDetailToRoutineExercise(details: DetailsEntity)

    /**
     * Elimina un detalle específico.
     */
    @Delete
    suspend fun deleteDetail(detail: DetailsEntity)

    /**
     * Actualiza un detalle específico.
     */
    @Update
    suspend fun updateDetail(detail: DetailsEntity)

    /**
     * Obtiene los detalles de un ejercicio en una rutina de forma puntual (suspend).
     */
    @Query("SELECT * FROM details_table WHERE routineDetailsId = :routineId AND exerciseDetailsId = :exerciseId")
    suspend fun getDetailOfRoutineAndExercise(
        routineId: Long,
        exerciseId: Long
    ): List<DetailsEntity>

    /**
     * Obtiene los detalles de un ejercicio en una rutina como Flow reactivo.
     */
    @Query("SELECT * FROM details_table WHERE routineDetailsId = :routineId AND exerciseDetailsId = :exerciseId")
    fun getDetailOfRoutineAndExerciseFlow(
        routineId: Long,
        exerciseId: Long
    ): Flow<List<DetailsEntity>>

    /**
     * Elimina el último detalle agregado para un ejercicio en una rutina, usando subconsulta para obtener el último detalleId.
     */
    @Query(
        """
        DELETE FROM details_table 
        WHERE detailsId = (
            SELECT detailsId FROM details_table 
            WHERE routineDetailsId = :routineId AND exerciseDetailsId = :exerciseId 
            ORDER BY detailsId DESC 
            LIMIT 1
        )
        """
    )
    suspend fun deleteLastDetail(routineId: Long, exerciseId: Long)
}
package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    @Query("SELECT * FROM training_table")
    fun getAllTraining(): Flow<List<TrainingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraining(training: TrainingEntity)

    @Delete
    suspend fun deleteTraining(training: TrainingEntity)

    @Query("DELETE FROM training_table")
    suspend fun deleteAllTraining()

    @Transaction
    @Query("SELECT * FROM training_table")
    fun getTrainingWithWeeksAndRoutines(): Flow<List<TrainingWithWeeksAndRoutines>>
}

//SELECT columna
//FROM tabla
//WHERE columna IN (SELECT columna FROM otra_tabla WHERE condición)

//SELECT
//(SELECT COUNT(*) FROM semanas WHERE entrenos_id = entrenos.id) AS numero_semanas,
//(SELECT SUM(numero_rutinas) FROM (SELECT COUNT(*) AS numero_rutinas FROM rutinas WHERE semanas_id IN (SELECT id FROM semanas WHERE entrenos_id = entrenos.id) GROUP BY semanas_id)) AS numero_rutinas
//FROM entrenos
//WHERE id = tu_id_de_entreno;
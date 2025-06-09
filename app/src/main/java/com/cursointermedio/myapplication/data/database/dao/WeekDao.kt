package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import kotlinx.coroutines.flow.Flow

@Dao
interface WeekDao {

    // Obtiene todas las semanas asociadas a un entrenamiento como un Flow reactivo.
    @Query("SELECT * FROM week_table WHERE :trainingId = trainingWeekId")
    fun getWeeksTraining(trainingId: Long): Flow<List<WeekEntity>>

    // Inserta una semana para un entrenamiento y devuelve su ID.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeekToTraining(week: WeekEntity): Long

    // Elimina una semana específica.
    @Delete
    suspend fun deleteWeek(week: WeekEntity)

    // Obtiene todas las semanas de un entrenamiento con sus rutinas asociadas (relación uno a muchos).
    @Transaction
    @Query("SELECT * FROM week_table WHERE trainingWeekId = :trainingId")
    fun getAllWeeksWithRoutines(trainingId: Long): Flow<List<WeekWithRoutines>>

    // Obtiene una única semana con sus rutinas asociadas.
    @Transaction
    @Query("SELECT * FROM week_table WHERE weekId = :weekId")
    fun getWeekWithRoutines(weekId: Long): WeekWithRoutines

    // Devuelve el nombre de un entrenamiento específico.
    @Query("SELECT name FROM training_table WHERE trainingId = :trainingId")
    suspend fun getTrainingName(trainingId: Long): String
}
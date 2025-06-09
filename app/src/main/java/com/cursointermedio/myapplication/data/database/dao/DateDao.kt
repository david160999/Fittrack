package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.cursointermedio.myapplication.data.database.entities.CategoryEntity
import com.cursointermedio.myapplication.data.database.entities.DateEntity
import com.cursointermedio.myapplication.data.database.entities.DateWithTrac
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.TracEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DateDao {

    // Inserta o reemplaza una fecha en la base de datos.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDate(dateEntity: DateEntity)

    // Obtiene una fecha específica por su ID, función suspend para llamada puntual.
    @Query("SELECT * FROM date_table WHERE dateId = :dateId")
    suspend fun getDate(dateId: String): DateEntity?

    // Obtiene una fecha como Flow para observar cambios en tiempo real.
    @Query("SELECT * FROM date_table WHERE dateId = :dateId")
    fun getDateFlow(dateId: String): Flow<DateEntity?>

    // Obtiene una lista de fechas con sus relaciones (DateWithTrac) para varios IDs, como Flow.
    @Query("SELECT * FROM date_table WHERE dateId IN (:dateList)")
    fun getDateListFlow(dateList: List<String>): Flow<List<DateWithTrac?>>

    // Consulta transaccional para obtener una fecha junto con su relación Trac de forma puntual.
    @Transaction
    @Query("SELECT * FROM date_table WHERE dateId = :dateId")
    suspend fun getDateWithTrac(dateId: String): DateWithTrac?

    // Lo mismo que arriba, pero en modo reactivo con Flow.
    @Transaction
    @Query("SELECT * FROM date_table WHERE dateId = :dateId")
    fun getDateWithTracFlow(dateId: String): Flow<DateWithTrac?>

    // Elimina una fecha específica.
    @Delete
    suspend fun deleteDate(dateEntity: DateEntity)

    // Actualiza la nota de una fecha específica.
    @Query("UPDATE date_table SET note = :note WHERE dateId = :dateId")
    suspend fun updateNote(dateId: String, note: String?)

    // Actualiza el peso corporal de una fecha específica.
    @Query("UPDATE date_table SET bodyWeight = :bodyWeight WHERE dateId = :dateId")
    suspend fun updateBodyWeight(dateId: String, bodyWeight: Float?)

    // Borra la nota (la pone en NULL) de una fecha.
    @Query("UPDATE date_table SET note = NULL WHERE dateId = :dateId")
    suspend fun deleteNote(dateId: String)

    // Borra el peso corporal (lo pone en NULL) de una fecha.
    @Query("UPDATE date_table SET bodyWeight = NULL WHERE dateId = :dateId")
    suspend fun deleteBodyWeight(dateId: String)

    // Borra la rutina asociada a un día en el calendario (pone routineId en NULL).
    @Query("UPDATE date_table SET routineId = NULL WHERE dateId = :dateId")
    suspend fun deleteRoutineCalendar(dateId: String)

    // Inserta o reemplaza un registro Trac.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrac(tracEntity: TracEntity)

    // Obtiene el registro Trac asociado a una fecha como Flow.
    @Query("SELECT * FROM trac_table WHERE dateId = :dateId")
    fun getTracByDateFlow(dateId: String): Flow<TracEntity?>

    // Actualiza un registro Trac existente.
    @Update
    suspend fun updateTrac(tracEntity: TracEntity)

    // Obtiene los IDs de fechas asociadas a una rutina como Flow.
    @Query("SELECT dateId FROM date_table WHERE routineId = :routineId")
    fun getDatesFromRoutine(routineId: Long): Flow<String>

    // Inserta o reemplaza una lista de fechas.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDateList(dateList: List<DateEntity>)

    // Elimina un registro Trac específico.
    @Delete
    suspend fun deleteTrac(trac: TracEntity)
}
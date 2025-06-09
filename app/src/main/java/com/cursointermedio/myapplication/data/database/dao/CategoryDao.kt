package com.cursointermedio.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cursointermedio.myapplication.data.database.entities.CategoryEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    /**
     * Obtiene todas las categorías de la base de datos como un Flow reactivo.
     * Devuelve una lista mutable de entidades CategoryEntity.
     */
    @Query("SELECT * FROM category_table")
    fun getAllCategories(): Flow<MutableList<CategoryEntity>>

    /**
     * Inserta una categoría en la base de datos.
     * Si ya existe una categoría con la misma clave primaria, la reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    /**
     * Elimina una categoría específica de la base de datos.
     */
    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}
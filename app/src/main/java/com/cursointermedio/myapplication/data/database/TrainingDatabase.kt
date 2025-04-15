package com.cursointermedio.myapplication.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cursointermedio.myapplication.data.database.dao.CategoryDao
import com.cursointermedio.myapplication.data.database.dao.DetailsDao
import com.cursointermedio.myapplication.data.database.dao.ExerciseDao
import com.cursointermedio.myapplication.data.database.dao.RoutineDao
import com.cursointermedio.myapplication.data.database.dao.TrainingDao
import com.cursointermedio.myapplication.data.database.dao.WeekDao
import com.cursointermedio.myapplication.data.database.entities.CategoryEntity
import com.cursointermedio.myapplication.data.database.entities.DetailsEntity
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.RoutineExerciseCrossRef
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.WeekEntity

@Database(
    entities = [TrainingEntity::class, WeekEntity::class, RoutineEntity::class, RoutineExerciseCrossRef::class, ExerciseEntity::class, DetailsEntity::class, CategoryEntity::class],
    version = 5
)
abstract class TrainingDatabase : RoomDatabase() {

    abstract fun getTrainingDao(): TrainingDao
    abstract fun getWeekDao(): WeekDao
    abstract fun getRoutineDao(): RoutineDao
    abstract fun getExerciseDao(): ExerciseDao
    abstract fun getDetailsDao(): DetailsDao
    abstract fun getCategoryDao(): CategoryDao
}
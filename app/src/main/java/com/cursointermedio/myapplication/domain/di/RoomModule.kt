package com.cursointermedio.myapplication.domain.di

import android.content.Context
import androidx.room.Room
import com.cursointermedio.myapplication.data.database.TrainingDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val QUOTE_DATABASE_NAME = "training_database"

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, TrainingDatabase::class.java, QUOTE_DATABASE_NAME).fallbackToDestructiveMigration().build()
        //createFromAsset("database/training.db").build()

    @Singleton
    @Provides
    fun provideTrainingDao(db: TrainingDatabase) = db.getTrainingDao()

    @Singleton
    @Provides
    fun provideWeekDao(db: TrainingDatabase) = db.getWeekDao()

    @Singleton
    @Provides
    fun provideRoutineDao(db: TrainingDatabase) = db.getRoutineDao()

    @Singleton
    @Provides
    fun provideCategoryDao(db: TrainingDatabase) = db.getCategoryDao()

    @Singleton
    @Provides
    fun provideExerciseDao(db: TrainingDatabase) = db.getExerciseDao()

    @Singleton
    @Provides
    fun provideDetailsDao(db: TrainingDatabase) = db.getDetailsDao()
}
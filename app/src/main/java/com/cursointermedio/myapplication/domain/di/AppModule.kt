package com.cursointermedio.myapplication.domain.di

import android.content.Context
import com.cursointermedio.myapplication.data.database.local.UserPreferences
import com.cursointermedio.myapplication.data.repository.UserPreferencesRepositoryImpl
import com.cursointermedio.myapplication.domain.model.UserSettings
import com.cursointermedio.myapplication.domain.repository.UserPreferencesRepository
import com.google.firebase.functions.dagger.Module
import com.google.firebase.functions.dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


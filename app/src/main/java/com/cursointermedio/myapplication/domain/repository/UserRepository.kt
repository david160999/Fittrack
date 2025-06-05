package com.cursointermedio.myapplication.domain.repository

import com.cursointermedio.myapplication.domain.model.UserData

interface UserRepository {
    suspend fun getUserData(): UserData
    fun signOut()
}
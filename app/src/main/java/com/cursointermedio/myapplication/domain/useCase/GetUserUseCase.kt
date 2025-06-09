package com.cursointermedio.myapplication.domain.useCase

import com.cursointermedio.myapplication.data.repository.UserRepositoryImpl
import com.cursointermedio.myapplication.data.repository.WeekRepository
import com.cursointermedio.myapplication.domain.model.UserData
import javax.inject.Inject

class GetUserUseCase@Inject constructor(
    private val repository: UserRepositoryImpl
){
    // Obtiene los datos del usuario actual.
    suspend fun getUserData(): UserData {
        return repository.getUserData()
    }

    // Cierra la sesión del usuario actual.
    fun signOut() {
        repository.signOut()
    }
}

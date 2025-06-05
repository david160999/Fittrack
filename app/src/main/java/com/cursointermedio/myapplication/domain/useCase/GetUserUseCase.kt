package com.cursointermedio.myapplication.domain.useCase

import com.cursointermedio.myapplication.data.repository.UserRepositoryImpl
import com.cursointermedio.myapplication.data.repository.WeekRepository
import com.cursointermedio.myapplication.domain.model.UserData
import javax.inject.Inject

class GetUserUseCase@Inject constructor(
    private val repository: UserRepositoryImpl,

    ) {

    suspend fun getUserData(): UserData {
        return  repository.getUserData()
    }

    fun signOut() {
        repository.signOut()
    }
}

package com.cursointermedio.myapplication.utils.extensions

import com.cursointermedio.myapplication.domain.model.UserData

sealed class UserDataUiSate {
    data object Loading : UserDataUiSate()
    data class Success(val userData: UserData) : UserDataUiSate()
    data class Error(val message: String) : UserDataUiSate()
}
package com.cursointermedio.myapplication.domain.model

import android.net.Uri

data class UserData(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: Uri
)
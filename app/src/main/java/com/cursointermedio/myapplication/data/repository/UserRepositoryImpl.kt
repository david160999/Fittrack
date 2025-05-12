package com.cursointermedio.myapplication.data.repository

import android.net.Uri
import com.cursointermedio.myapplication.domain.model.UserData
import com.cursointermedio.myapplication.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : UserRepository {

    override suspend fun getUserData(): UserData {
        val currentUser = auth.currentUser
        return UserData(
            id = currentUser?.uid ?: "",
            name = currentUser?.displayName ?: "Invitado",
            email = currentUser?.email ?: "No disponible",
            photoUrl = currentUser?.photoUrl ?: Uri.parse("android.resource://com.tuapp.nombre/drawable/avatar_default")
        )
    }
}
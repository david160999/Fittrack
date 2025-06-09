package com.cursointermedio.myapplication.data.repository

import android.net.Uri
import com.cursointermedio.myapplication.domain.model.UserData
import com.cursointermedio.myapplication.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : UserRepository {

    // Obtiene los datos del usuario autenticado desde FirebaseAuth.
    override suspend fun getUserData(): UserData {
        val currentUser = auth.currentUser  // Obtiene el usuario actual, si existe

        // Retorna un objeto UserData con la información del usuario autenticado.
        // Si no hay usuario, se usan valores por defecto (como "Invitado" o un avatar predeterminado).
        return UserData(
            id = currentUser?.uid ?: "",  // ID único del usuario o cadena vacía si no está autenticado
            name = currentUser?.displayName ?: "Invitado",  // Nombre visible o "Invitado" si es nulo
            email = currentUser?.email ?: "No disponible",  // Correo electrónico o mensaje por defecto
            photoUrl = currentUser?.photoUrl
                ?: Uri.parse("android.resource://com.tuapp.nombre/drawable/avatar_default") // Imagen por defecto si no tiene foto
        )
    }

    // Cierra la sesión del usuario actual en Firebase.
    override fun signOut() {
        auth.signOut()  // Llama a FirebaseAuth para cerrar sesión
    }
}
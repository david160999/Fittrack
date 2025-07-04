package com.cursointermedio.myapplication.ui.settings

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.domain.model.UserSettings
import com.cursointermedio.myapplication.domain.useCase.GetUserPreferencesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetUserUseCase
import com.cursointermedio.myapplication.ui.home.login.LoginActivity
import com.cursointermedio.myapplication.utils.extensions.UserDataUiSate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase
) : ViewModel() {

    // Estado con los datos del usuario (nombre, email, foto, etc)
    private val _userData = MutableStateFlow<UserDataUiSate>(UserDataUiSate.Loading)
    val userData: StateFlow<UserDataUiSate> = _userData

    // Estado con los ajustes de preferencia del usuario (tema, idioma, unidades, etc)
    private val _userSettingsData = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val userSettingsData: StateFlow<SettingsUiState> = _userSettingsData

    // Al inicializar el ViewModel, obtén los datos y ajustes del usuario
    init {
        getUserData()
        getUserSettingsData()
    }

    // Recupera los datos del usuario
    private fun getUserData() {
        viewModelScope.launch {
            try {
                _userData.value = UserDataUiSate.Success(getUserUseCase.getUserData())
            } catch (e: Exception) {
                _userData.value = UserDataUiSate.Error("Error")
                Log.e("getUserData", "Error al intentar recoger los datos del usuario")
            }
        }
    }

    // Recupera las preferencias de usuario y las expone como StateFlow
    private fun getUserSettingsData() {
        viewModelScope.launch {
            try {
                getUserPreferencesUseCase.userSettingsFlow
                    .flowOn(Dispatchers.IO)
                    .catch { e ->
                        _userSettingsData.value = SettingsUiState.Error("Error")
                        Log.e("getUserData", e.message.toString())
                    }
                    .collectLatest { userSettings ->
                        _userSettingsData.value = SettingsUiState.Success(userSettings)
                    }
            } catch (e: Exception) {
                Log.e("getUserData", "Error al intentar recoger los datos del usuario")
            }
        }
    }

    // Guarda las preferencias del usuario (puede llamarse desde un hilo de fondo)
    suspend fun saveUserSettingsData(settings: UserSettings) {
        viewModelScope.launch {
            try {
                getUserPreferencesUseCase.saveUserSettings(settings)
            } catch (e: Exception) {
                Log.e("getUserData", e.message.toString())
            }
        }
    }

    // Actualiza solo el estado de DarkMode y guarda las preferencias
    fun onDarkModeToggled(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = getUserPreferencesUseCase.userSettingsFlow.first()
            val updatedSettings = currentSettings.copy(isDarkMode = enabled)
            getUserPreferencesUseCase.saveUserSettings(updatedSettings)
        }
    }

    // Cierra sesión y navega a LoginActivity
    fun signOut(context: Context) {
        getUserUseCase.signOut()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(context, intent, null)
    }
}
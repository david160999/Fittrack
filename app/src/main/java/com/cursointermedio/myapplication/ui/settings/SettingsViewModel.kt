package com.cursointermedio.myapplication.ui.settings

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.domain.model.UserData
import com.cursointermedio.myapplication.domain.model.UserSettings
import com.cursointermedio.myapplication.domain.useCase.GetDateUseCase
import com.cursointermedio.myapplication.domain.useCase.GetExercisesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import com.cursointermedio.myapplication.domain.useCase.GetUserPreferencesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetUserUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import com.cursointermedio.myapplication.ui.home.login.LoginActivity
import com.cursointermedio.myapplication.ui.week.WeekUiState
import com.cursointermedio.myapplication.utils.extensions.UserDataUiSate
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    private val _userData = MutableStateFlow<UserDataUiSate>(UserDataUiSate.Loading)
    val userData: StateFlow<UserDataUiSate> = _userData

    private val _userSettingsData = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val userSettingsData: StateFlow<SettingsUiState> = _userSettingsData

    init {
        getUserData()
        getUserSettingsData()
    }

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

    suspend fun saveUserSettingsData(settings: UserSettings) {
        viewModelScope.launch {
            try {
                getUserPreferencesUseCase.saveUserSettings(settings)

            } catch (e: Exception) {
                Log.e("getUserData", e.message.toString())
            }
        }
    }

    fun onDarkModeToggled(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = getUserPreferencesUseCase.userSettingsFlow.first()
            val updatedSettings = currentSettings.copy(isDarkMode = enabled)
            getUserPreferencesUseCase.saveUserSettings(updatedSettings)
        }
    }

    fun signOut(context: Context) {
        getUserUseCase.signOut()

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(context, intent, null)
    }
}
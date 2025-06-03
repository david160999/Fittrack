package com.cursointermedio.myapplication.ui.settings

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.domain.model.UserData
import com.cursointermedio.myapplication.domain.useCase.GetDateUseCase
import com.cursointermedio.myapplication.domain.useCase.GetExercisesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
import com.cursointermedio.myapplication.domain.useCase.GetUserUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import com.cursointermedio.myapplication.ui.week.WeekUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData


    init {
        getUserData()
    }

    private fun getUserData(){
        viewModelScope.launch {
            try {
                _userData.value = getUserUseCase.getUserData()

            }catch (e:Exception){
                Log.e("getUserData", "Error al intentar recoger los datos del usuario")
            }
        }
    }

}
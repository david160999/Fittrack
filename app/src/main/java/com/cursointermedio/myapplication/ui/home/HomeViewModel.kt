package com.cursointermedio.myapplication.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursointermedio.myapplication.data.database.entities.DateEntity
import com.cursointermedio.myapplication.data.database.entities.TracEntity
import com.cursointermedio.myapplication.domain.model.UserData
import com.cursointermedio.myapplication.domain.useCase.GetDateUseCase
import com.cursointermedio.myapplication.domain.useCase.GetTrainingUseCase
import com.cursointermedio.myapplication.domain.useCase.GetUserPreferencesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetUserUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import com.cursointermedio.myapplication.ui.settings.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getDateUseCase: GetDateUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase
) : ViewModel() {
    private val currentDay = LocalDate.now()

    private val _userSettingsData = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val userSettingsData: StateFlow<SettingsUiState> = _userSettingsData

    private val _dateInfo = MutableStateFlow<DateEntity?>(null)
    val dateInfo: StateFlow<DateEntity?> get() = _dateInfo

    private val _tracInfo = MutableStateFlow<TracEntity?>(null)
    val tracInfo: StateFlow<TracEntity?> get() = _tracInfo

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> get() = _userData

    private val _userWeight = MutableStateFlow<String?>(null)
    val userWeight: StateFlow<String?> get() = _userWeight


    init {
        getDateFlow()
        getTracByDateFlow()
        getUserSettingsData()

        viewModelScope.launch {
            try {
                _userData.value = getUserUseCase.getUserData()
            } catch (e: Exception) {
            }
        }
    }

    private fun getTracByDateFlow() {
        viewModelScope.launch {
            try {
                getDateUseCase.getTracByDateFlow(currentDay.toString()).collectLatest {
                    _tracInfo.value = it
                }

            } catch (e: CancellationException) {
                // OK, la corutina fue cancelada porque el fragmento/VM murió
            } catch (e: Exception) {
                Log.e("getTracByDate", "Error recoger los datos del trac del dia $currentDay", e)
            }
        }
    }

    private fun getDateFlow() {
        viewModelScope.launch {
            try {
                combine(
                    getDateUseCase.getDateFlow(currentDay.toString()),
                    getUserPreferencesUseCase.userSettingsFlow
                ) { date, userSettings ->

                    val weightValue = date?.bodyWeight ?: 0f
                    val unit = if (userSettings.isWeightKgMode) "kg" else "lbs"

                    Pair("$weightValue $unit", date)
                }.collectLatest { (weightString, date) ->
                    _userWeight.value = weightString
                    _dateInfo.value = date
                }

            } catch (e: CancellationException) {
                // OK, la corutina fue cancelada porque el fragmento/VM murió
            } catch (e: Exception) {
                Log.e("getDate", "Error recoger los datos de date $currentDay", e)
            }
        }
    }

    fun insertOrUpdateTrac(trac: TracEntity) {
        viewModelScope.launch {
            try {
                val date = _dateInfo.value

                if (date == null) {
                    val newDate = DateEntity(trac.dateId, null, null, null)
                    getDateUseCase.insertOrUpdateDate(newDate)
                }

                getDateUseCase.insertOrUpdateTrac(trac)
            } catch (e: Exception) {
                Log.e("getTracByDate", "Error intentar insertar los datos del trac", e)
            }
        }
    }

    fun deleteTrac() {
        viewModelScope.launch {
            try {
                getDateUseCase.deleteTrac(_tracInfo.value!!)

            } catch (e: Exception) {
                Log.e("getTracByDate", "Error recoger los datos del trac del dia $currentDay", e)
            }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            try {
                val dateId = _dateInfo.value?.dateId
                if (dateId != null) {
                    getDateUseCase.deleteNote(dateId)
                }
            } catch (e: Exception) {
                Log.e("getTracByDate", "Error recoger los datos del trac del dia $currentDay", e)
            }
        }
    }

    fun deleteBodyWeight() {
        viewModelScope.launch {
            try {
                val dateId = _dateInfo.value?.dateId
                if (dateId != null) {
                    getDateUseCase.deleteBodyWeight(dateId)
                }
            } catch (e: Exception) {
                Log.e("getTracByDate", "Error recoger los datos del trac del dia $currentDay", e)
            }
        }
    }

    fun updateBodyWeight(weight: Float) {
        viewModelScope.launch {
            try {
                val date = _dateInfo.value

                if (date == null) {
                    val newDate = DateEntity(currentDay.toString(), null, bodyWeight = weight, null)
                    getDateUseCase.insertOrUpdateDate(newDate)
                } else {
                    getDateUseCase.updateBodyWeight(
                        dateId = currentDay.toString(),
                        bodyWeight = weight
                    )
                }

            } catch (e: Exception) {
                Log.e(
                    "getTracByDate",
                    "Error al actualizar los datos del peso en el dia $currentDay", e
                )
            }
        }
    }

    fun updateNote(notes: String) {
        viewModelScope.launch {
            try {
                val date = _dateInfo.value

                if (date == null) {
                    val newDate = DateEntity(currentDay.toString(), note = notes, null, null)
                    getDateUseCase.insertOrUpdateDate(newDate)
                } else {
                    getDateUseCase.updateNote(
                        dateId = currentDay.toString(),
                        note = notes
                    )
                }

            } catch (e: Exception) {
                Log.e(
                    "updateNote",
                    "Error al actualizar los datos de las notas en el dia $currentDay", e
                )
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
}
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
import com.cursointermedio.myapplication.domain.useCase.GetUserUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getDateUseCase: GetDateUseCase
) : ViewModel() {
    private val currentDay = LocalDate.now()

    private val _dateInfo = MutableStateFlow<DateEntity?>(null)
    val dateInfo: StateFlow<DateEntity?> get() = _dateInfo

    private val _tracInfo = MutableStateFlow<TracEntity?>(null)
    val tracInfo: StateFlow<TracEntity?> get() = _tracInfo

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> get() = _userData


    init {
        getDateFlow()
        getTracByDateFlow()
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

            } catch (e: Exception) {
                Log.e("getTracByDate", "Error recoger los datos del trac del dia $currentDay", e)
            }
        }
    }

    private fun getDateFlow() {
        viewModelScope.launch {
            try {
                getDateUseCase.getDateFlow(currentDay.toString()).collectLatest {
                    _dateInfo.value = it
                }

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
                if (dateId!=null){
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
                if (dateId!=null){
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
}
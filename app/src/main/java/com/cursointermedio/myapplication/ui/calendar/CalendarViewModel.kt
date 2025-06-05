package com.cursointermedio.myapplication.ui.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.newStringBuilder
import com.cursointermedio.myapplication.data.database.entities.DateEntity
import com.cursointermedio.myapplication.data.database.entities.DateWithTrac
import com.cursointermedio.myapplication.data.database.entities.TracEntity
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.domain.useCase.GetDateUseCase
import com.cursointermedio.myapplication.domain.useCase.GetTrainingUseCase
import com.cursointermedio.myapplication.domain.useCase.GetUserPreferencesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getDateUseCase: GetDateUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase
) : ViewModel() {
    private val currentDay = LocalDate.now()

    private val _dateSelected = MutableStateFlow<DateWithTrac?>(null)
    val dateSelected: StateFlow<DateWithTrac?> = _dateSelected

    private val _dateList = MutableStateFlow<List<DateWithTrac?>>(emptyList())
    val dateList: StateFlow<List<DateWithTrac?>> = _dateList

    private val _userWeight = MutableStateFlow<String?>(null)
    val userWeight: StateFlow<String?> get() = _userWeight

    init {
        getSelectedDateWithTracFlow(currentDay.toString())
    }

    fun getDateListFlow(dateList: List<String>) {
        viewModelScope.launch {
            try {
                getDateUseCase.getDateListFlow(dateList).collectLatest {
                    _dateList.value = it
                }

            } catch (e: Exception) {
                Log.e("getDatelist", "Error al recoger los datos de Dates de la lista", e)
            }
        }
    }

    fun getSelectedDateWithTracFlow(dateId: String) {
        viewModelScope.launch {
            try {
                combine( getDateUseCase.getDateWithTracFlow(dateId), getUserPreferencesUseCase.userSettingsFlow)
                { dateInfo, userSettings ->

                    val weightValue = dateInfo?.dateEntity?.bodyWeight ?: 0f
                    val unit = if (userSettings.isWeightKgMode) "kg" else "lbs"

                    Pair("$weightValue $unit", dateInfo)

                }.collectLatest { (weightString, dateInfo) ->
                    _dateSelected.value = dateInfo
                    _userWeight.value = weightString
                }
            } catch (e: Exception) {
                Log.e("getSelectedDateFlow", "Error al recoger los datos del dia seleccionado", e)
            }
        }
    }

    fun deleteTrac() {
        viewModelScope.launch {
            try {
                val tracEntity = _dateSelected.value?.tracEntity
                if (tracEntity != null) {
                    getDateUseCase.deleteTrac(tracEntity)
                }
            } catch (e: Exception) {
                Log.e("getTracByDate", "Error recoger los datos del trac del dia $currentDay", e)
            }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            try {
                val dateId = _dateSelected.value?.dateEntity?.dateId
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
                val dateId = _dateSelected.value?.dateEntity?.dateId
                if (dateId != null) {
                    getDateUseCase.deleteBodyWeight(dateId)
                }
            } catch (e: Exception) {
                Log.e("getTracByDate", "Error recoger los datos del trac del dia $currentDay", e)
            }
        }
    }

    fun updateBodyWeight(weight: Float, dateId: String) {
        viewModelScope.launch {
            try {
                val date = _dateSelected.value?.dateEntity

                if (date == null) {
                    val newDate = DateEntity(dateId, null, bodyWeight = weight, null)
                    getDateUseCase.insertOrUpdateDate(newDate)
                } else {
                    getDateUseCase.updateBodyWeight(
                        dateId = dateId,
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

    fun updateNote(notes: String, dateId: String) {
        viewModelScope.launch {
            try {
                val date = _dateSelected.value?.dateEntity

                if (date == null) {
                    val newDate = DateEntity(dateId, note = notes, null, null)
                    getDateUseCase.insertOrUpdateDate(newDate)
                } else {
                    getDateUseCase.updateNote(
                        dateId = dateId,
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

    fun insertOrUpdateTrac(trac: TracEntity, dateId:String) {
        viewModelScope.launch {
            try {
                val date = _dateSelected.value?.dateEntity

                if (date == null) {
                    val newDate = DateEntity(dateId, null, null, null)
                    getDateUseCase.insertOrUpdateDate(newDate)
                }

                val newTrac = trac.copy(dateId = dateId)
                getDateUseCase.insertOrUpdateTrac(newTrac)

            } catch (e: Exception) {
                Log.e("getTracByDate", "Error intentar insertar los datos del trac", e)
            }
        }
    }
}
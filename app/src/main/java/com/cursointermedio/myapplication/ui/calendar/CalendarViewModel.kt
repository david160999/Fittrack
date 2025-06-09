package com.cursointermedio.myapplication.ui.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.newStringBuilder
import com.cursointermedio.myapplication.data.database.entities.DateEntity
import com.cursointermedio.myapplication.data.database.entities.DateWithTrac
import com.cursointermedio.myapplication.data.database.entities.TracEntity
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.domain.useCase.GetDateUseCase
import com.cursointermedio.myapplication.domain.useCase.GetExercisesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetRoutineUseCase
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
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val getRoutineUseCase: GetRoutineUseCase,
    private val getExercisesUseCase: GetExercisesUseCase

) : ViewModel() {
    private val currentDay = LocalDate.now()

    // Estado para la fecha seleccionada con información combinada (fecha + trac + rutina)
    private val _dateSelected = MutableStateFlow<DateWithTrac?>(null)
    val dateSelected: StateFlow<DateWithTrac?> = _dateSelected

    // Estado para la lista de fechas con información relacionada
    private val _dateList = MutableStateFlow<List<DateWithTrac?>>(emptyList())
    val dateList: StateFlow<List<DateWithTrac?>> = _dateList

    // Estado para el peso del usuario formateado como String
    private val _userWeight = MutableStateFlow<String?>(null)
    val userWeight: StateFlow<String?> get() = _userWeight

    // Estado para información de rutina actual
    private val _routineInfo = MutableStateFlow<RoutineModel?>(null)
    val routineInfo: StateFlow<RoutineModel?> get() = _routineInfo

    init {
        // Inicialmente carga datos del día actual
        getSelectedDateWithTracFlow(currentDay.toString())
    }

    // Obtiene el flujo de lista de fechas con información asociada y actualiza el estado
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

    // Obtiene el flujo de la fecha seleccionada, combinando con preferencias de usuario
    fun getSelectedDateWithTracFlow(dateId: String) {
        viewModelScope.launch {
            try {
                combine(
                    getDateUseCase.getDateWithTracFlow(dateId),
                    getUserPreferencesUseCase.userSettingsFlow
                )
                { dateInfo, userSettings ->
                    var routine: RoutineModel? = null
                    var exerciseNum = 0

                    // Si la fecha tiene rutina asociada, obtiene info de rutina y número de ejercicios
                    if (dateInfo?.dateEntity?.routineId != null) {
                        exerciseNum = getExercisesUseCase.getExerciseFromRoutineCount(dateInfo.dateEntity.routineId)
                        routine = getRoutineUseCase.getRoutineById(dateInfo.dateEntity.routineId)
                    }

                    // Formatea peso con unidad según configuración del usuario
                    val weightValue = dateInfo?.dateEntity?.bodyWeight ?: 0f
                    val unit = if (userSettings.isWeightKgMode) "kg" else "lbs"

                    Triple(
                        "$weightValue $unit",
                        dateInfo,
                        routine?.copy(exerciseCount = exerciseNum)
                    )
                }.collectLatest { (weightString, dateInfo, routineInfo) ->
                    // Actualiza los estados para la UI
                    _dateSelected.value = dateInfo
                    _userWeight.value = weightString
                    _routineInfo.value = routineInfo
                }
            } catch (e: Exception) {
                Log.e("getSelectedDateFlow", "Error al recoger los datos del dia seleccionado", e)
            }
        }
    }

    // Función para eliminar datos de trac asociados a la fecha seleccionada
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

    // Función para eliminar nota asociada a la fecha seleccionada
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

    // Función para eliminar peso corporal asociado a la fecha seleccionada
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

    // Actualiza o inserta el peso corporal para una fecha dada
    fun updateBodyWeight(weight: Float, dateId: String) {
        viewModelScope.launch {
            try {
                val date = _dateSelected.value?.dateEntity

                if (date == null) {
                    // Si no existe la fecha, crea una nueva con peso
                    val newDate = DateEntity(dateId, null, bodyWeight = weight, null)
                    getDateUseCase.insertOrUpdateDate(newDate)
                } else {
                    // Si existe, actualiza el peso corporal
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

    // Actualiza o inserta una nota para una fecha dada
    fun updateNote(notes: String, dateId: String) {
        viewModelScope.launch {
            try {
                val date = _dateSelected.value?.dateEntity

                if (date == null) {
                    // Si no existe la fecha, crea una nueva con la nota
                    val newDate = DateEntity(dateId, note = notes, null, null)
                    getDateUseCase.insertOrUpdateDate(newDate)
                } else {
                    // Si existe, actualiza la nota
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

    // Inserta o actualiza datos de trac para una fecha dada
    fun insertOrUpdateTrac(trac: TracEntity, dateId: String) {
        viewModelScope.launch {
            try {
                val date = _dateSelected.value?.dateEntity

                if (date == null) {
                    // Si no existe la fecha, crea una nueva vacía para luego añadir trac
                    val newDate = DateEntity(dateId, null, null, null)
                    getDateUseCase.insertOrUpdateDate(newDate)
                }

                // Inserta o actualiza la entidad trac asociada a la fecha
                val newTrac = trac.copy(dateId = dateId)
                getDateUseCase.insertOrUpdateTrac(newTrac)

            } catch (e: Exception) {
                Log.e("getTracByDate", "Error intentar insertar los datos del trac", e)
            }
        }
    }

    // Elimina la rutina asociada a la fecha seleccionada
    fun deleteRoutine() {
        viewModelScope.launch {
            try {
                val dateId = _dateSelected.value?.dateEntity?.dateId
                if (dateId != null) {
                    getDateUseCase.deleteRoutineCalendar(dateId)
                }
            } catch (e: Exception) {
                Log.e("deleteRoutine", "Error al intentar borrar la ruitna del calendario", e)
            }
        }
    }
}
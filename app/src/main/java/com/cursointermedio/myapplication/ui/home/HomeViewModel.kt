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
import com.cursointermedio.myapplication.domain.useCase.GetUserPreferencesUseCase
import com.cursointermedio.myapplication.domain.useCase.GetUserUseCase
import com.cursointermedio.myapplication.ui.settings.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getDateUseCase: GetDateUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase
) : ViewModel() {

    // Día actual (usado para consultar/actualizar datos diarios)
    private val currentDay = LocalDate.now()

    // Estado de los ajustes del usuario (flujo observable)
    private val _userSettingsData = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val userSettingsData: StateFlow<SettingsUiState> = _userSettingsData

    // Estado de la entidad DateEntity (datos diarios: nota, peso, etc)
    private val _dateInfo = MutableStateFlow<DateEntity?>(null)
    val dateInfo: StateFlow<DateEntity?> get() = _dateInfo

    // Estado del TracEntity (datos de trac del día)
    private val _tracInfo = MutableStateFlow<TracEntity?>(null)
    val tracInfo: StateFlow<TracEntity?> get() = _tracInfo

    // Datos básicos del usuario (nombre, foto, etc)
    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> get() = _userData

    // Peso del usuario en string con unidad (por ejemplo "75.0 kg")
    private val _userWeight = MutableStateFlow<String?>(null)
    val userWeight: StateFlow<String?> get() = _userWeight

    // Inicialización del ViewModel: carga todos los datos observables
    init {
        getDateFlow()
        getTracByDateFlow()
        getUserSettingsData()

        // Cargar información básica del usuario de forma asíncrona
        viewModelScope.launch {
            try {
                _userData.value = getUserUseCase.getUserData()
            } catch (e: Exception) {
                // Manejar error si es necesario
            }
        }
    }

    // Flujo que observa y actualiza el Trac del día
    private fun getTracByDateFlow() {
        viewModelScope.launch {
            try {
                getDateUseCase.getTracByDateFlow(currentDay.toString()).collectLatest {
                    _tracInfo.value = it
                }
            } catch (e: CancellationException) {
                // Corrutina cancelada (normal si el fragmento/VM muere)
            } catch (e: Exception) {
                Log.e("getTracByDate", "Error recoger los datos del trac del dia $currentDay", e)
            }
        }
    }

    // Flujo que observa y actualiza los datos diarios y el peso formateado
    private fun getDateFlow() {
        viewModelScope.launch {
            try {
                combine(
                    getDateUseCase.getDateFlow(currentDay.toString()),
                    getUserPreferencesUseCase.userSettingsFlow
                ) { date, userSettings ->
                    // Formatea el peso según la preferencia del usuario
                    val weightValue = date?.bodyWeight ?: 0f
                    val unit = if (userSettings.isWeightKgMode) "kg" else "lbs"
                    Pair("$weightValue $unit", date)
                }.collectLatest { (weightString, date) ->
                    _userWeight.value = weightString
                    _dateInfo.value = date
                }
            } catch (e: CancellationException) {
                // Corrutina cancelada (normal si el fragmento/VM muere)
            } catch (e: Exception) {
                Log.e("getDate", "Error recoger los datos de date $currentDay", e)
            }
        }
    }

    // Inserta o actualiza un TracEntity para el día actual
    fun insertOrUpdateTrac(trac: TracEntity) {
        viewModelScope.launch {
            try {
                val date = _dateInfo.value

                if (date == null) {
                    // Si la fecha no existe, la crea primero
                    val newDate = DateEntity(trac.dateId, null, null, null)
                    getDateUseCase.insertOrUpdateDate(newDate)
                }

                // Inserta o actualiza el Trac
                getDateUseCase.insertOrUpdateTrac(trac)
            } catch (e: Exception) {
                Log.e("getTracByDate", "Error intentar insertar los datos del trac", e)
            }
        }
    }

    // Elimina el Trac del día actual
    fun deleteTrac() {
        viewModelScope.launch {
            try {
                getDateUseCase.deleteTrac(_tracInfo.value!!)
            } catch (e: Exception) {
                Log.e("getTracByDate", "Error recoger los datos del trac del dia $currentDay", e)
            }
        }
    }

    // Elimina la nota del día actual
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

    // Elimina el peso registrado del día actual
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

    // Actualiza el peso para el día actual (si no existe la fecha, la crea)
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

    // Actualiza la nota para el día actual (si no existe la fecha, la crea)
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

    // Observa y expone los datos de configuración del usuario
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
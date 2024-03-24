package com.cursointermedio.myapplication.ui.week

import android.adservices.adid.AdId
import androidx.lifecycle.ViewModel
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.useCase.GetTrainingUseCase
import com.cursointermedio.myapplication.domain.useCase.GetWeekUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeekViewModel @Inject constructor(
    private val getWeekUseCase: GetWeekUseCase
) : ViewModel() {

    fun getWeeksWithRoutines(trainingId: Int): Flow<List<WeekWithRoutines>> = getWeekUseCase.getWeeksWithRoutines(trainingId)

}
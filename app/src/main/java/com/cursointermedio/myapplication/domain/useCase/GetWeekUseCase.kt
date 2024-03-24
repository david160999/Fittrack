package com.cursointermedio.myapplication.domain.useCase

import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.data.database.entities.toDatabase
import com.cursointermedio.myapplication.data.repository.TrainingRepository
import com.cursointermedio.myapplication.data.repository.WeekRepository
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeekUseCase @Inject constructor(
    private val repository: WeekRepository
) {
    operator fun invoke(trainingId:Int): Flow<List<WeekModel>> {
        return repository.getWeeksTrainingFromDatabase(trainingId)
    }

    suspend fun insertWeekToTraining(week : WeekModel) {
        return repository.insertWeekToTraining(week.toDatabase())
    }

    fun getWeeksWithRoutines(trainingId: Int) : Flow<List<WeekWithRoutines>> = repository.getWeeksWithRoutines(trainingId)


    suspend fun deleteWeek(week: WeekModel){
        return repository.deleteWeek(week)
    }
}
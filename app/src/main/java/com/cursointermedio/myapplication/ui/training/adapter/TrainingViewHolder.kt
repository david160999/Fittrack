package com.cursointermedio.myapplication.ui.training.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import java.util.concurrent.ExecutionException

class TrainingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemTrainingBinding.bind(view)
    private lateinit var numWeeks: String
    private lateinit var numRoutines: String
    private var trainingId: Int = 0

    fun bind(
        trainingItemResponse: TrainingWithWeeksAndRoutines,
        onItemSelected: (Int) -> Unit
    ) {
        numWeeks = try {
            trainingItemResponse.weekWithRoutinesList.size.toString()
        } catch (e: Exception) {
            " 0"
        }

        numRoutines = try {
            trainingItemResponse.weekWithRoutinesList[0].routineList.size.toString()
        } catch (e: Exception) {
            " 0"
        }

        binding.tvTitle.text = trainingItemResponse.training.name
        binding.tvNumTrainings.text = numRoutines
        binding.tvNumCurrentsWeeks.text = numWeeks

        binding.root.setOnClickListener {
            trainingId = trainingItemResponse.training.trainingId!!
            onItemSelected(trainingId)
        }

    }
}
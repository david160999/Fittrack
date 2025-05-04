package com.cursointermedio.myapplication.ui.training.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.TrainingEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.databinding.ItemWeekBinding
import com.cursointermedio.myapplication.ui.week.adapter.WeekViewHolder

class TrainingAdapter(
    private val onItemSelected: (Long) -> Unit,
    private val menuActions: TrainingMenuActions

) : ListAdapter<TrainingsWithWeekAndRoutineCounts, TrainingViewHolder>(TrainingDiffCallback()) {

    class TrainingDiffCallback : DiffUtil.ItemCallback<TrainingsWithWeekAndRoutineCounts>() {
        override fun areItemsTheSame(
            oldItem: TrainingsWithWeekAndRoutineCounts,
            newItem: TrainingsWithWeekAndRoutineCounts
        ): Boolean {
            return oldItem.training.trainingId == newItem.training.trainingId
        }

        override fun areContentsTheSame(
            oldItem: TrainingsWithWeekAndRoutineCounts,
            newItem: TrainingsWithWeekAndRoutineCounts
        ): Boolean {
            // Compara el `TrainingEntity` y la lista de `WeekEntity`
            return oldItem.training == newItem.training
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val binding =
            ItemTrainingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrainingViewHolder(binding, parent.context)
    }


    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onItemSelected, menuActions)
    }

}


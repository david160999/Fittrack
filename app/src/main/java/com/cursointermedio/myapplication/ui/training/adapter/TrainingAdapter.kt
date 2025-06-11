package com.cursointermedio.myapplication.ui.training.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding

class TrainingAdapter(
    private val onItemSelected: (Long) -> Unit,            // Callback cuando se selecciona un entrenamiento
    private val menuActions: TrainingMenuActions            // Acciones del menú contextual
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
            return oldItem.training == newItem.training
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val binding = ItemTrainingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrainingViewHolder(binding)
    }

    // Asocia los datos al ViewHolder y ajusta el margen inferior si es el último ítem
    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onItemSelected, menuActions)

        // Ajusta el margen inferior: más grande para el último ítem, menor para los demás
        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
        if (position == currentList.lastIndex) {
            params.bottomMargin = 200 // Margen inferior grande para el último
        } else {
            params.bottomMargin = 20 // Margen estándar
        }
        holder.itemView.layoutParams = params
    }
}
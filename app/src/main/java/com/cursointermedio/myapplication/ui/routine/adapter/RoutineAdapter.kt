package com.cursointermedio.myapplication.ui.routine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.ui.training.adapter.TrainingViewHolder
import com.cursointermedio.myapplication.ui.week.adapter.WeekViewHolder

class RoutineAdapter(
    private val onItemSelected: (Long) -> Unit
) : ListAdapter<RoutineEntity, RoutineViewHolder>(RoutineDiffCallback()) {

    class RoutineDiffCallback : DiffUtil.ItemCallback<RoutineEntity>() {
        override fun areItemsTheSame(oldItem: RoutineEntity, newItem: RoutineEntity): Boolean {
            // Aquí defines cuándo dos items son el mismo, típicamente por ID
            return oldItem.routineId == newItem.routineId
        }

        override fun areContentsTheSame(oldItem: RoutineEntity, newItem: RoutineEntity): Boolean {
            // Aquí defines si el contenido de los items es el mismo
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val binding = ItemTrainingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoutineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = getItem(position)
        holder.bind(routine, onItemSelected)
    }

}
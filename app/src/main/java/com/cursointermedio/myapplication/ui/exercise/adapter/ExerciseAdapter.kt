package com.cursointermedio.myapplication.ui.exercise.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.cursointermedio.myapplication.databinding.ItemExerciseBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.ui.routine.ExerciseMenuActions

class ExerciseAdapter(
    private val onItemSelected: (Long) -> Unit,
    private val menuActions: ExerciseMenuActions
) : ListAdapter<ExerciseModel, ExerciseViewHolder>(ExerciseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onItemSelected, menuActions)
    }

    class ExerciseDiffCallback : DiffUtil.ItemCallback<ExerciseModel>() {

        override fun areItemsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            return oldItem == newItem
        }
    }
}
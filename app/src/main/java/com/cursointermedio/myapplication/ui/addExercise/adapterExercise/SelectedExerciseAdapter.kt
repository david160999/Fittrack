package com.cursointermedio.myapplication.ui.addExercise.adapterExercise

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.ItemSelectedexerciseBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel

class SelectedExerciseAdapter(
    private val onItemSelected: (ExerciseModel) -> Unit
) : ListAdapter<ExerciseModel, SelectedExerciseViewHolder>(ExerciseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedExerciseViewHolder {
        val binding = ItemSelectedexerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectedExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectedExerciseViewHolder, position: Int) {
        holder.bind(getItem(position), onItemSelected)
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


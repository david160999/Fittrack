package com.cursointermedio.myapplication.ui.addExercise.adapterExercise

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.databinding.ItemAddexerciseBinding
import com.cursointermedio.myapplication.databinding.ItemCategoryBinding
import com.cursointermedio.myapplication.databinding.ItemSelectedexerciseBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.ui.addExercise.adapterCategory.CategoryViewHolder


class AddExerciseAdapter(
    private val onItemSelected: (ExerciseModel) -> Unit
) : ListAdapter<ExerciseModel, AddExerciseViewHolder>(ExerciseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddExerciseViewHolder {
        val binding = ItemAddexerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddExerciseViewHolder, position: Int) {
        holder.bind(getItem(position), onItemSelected)
    }

    class ExerciseDiffCallback : DiffUtil.ItemCallback<ExerciseModel>() {
        override fun areItemsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            // Asume que ExerciseModel tiene un id único
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            // Puedes ajustar según los campos relevantes
            return oldItem == newItem
        }
    }
}


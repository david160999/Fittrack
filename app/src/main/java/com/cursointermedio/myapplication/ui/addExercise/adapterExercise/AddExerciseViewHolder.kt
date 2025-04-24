package com.cursointermedio.myapplication.ui.addExercise.adapterExercise

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.databinding.ItemAddexerciseBinding
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey

class AddExerciseViewHolder(private val binding: ItemAddexerciseBinding,
) :
    RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("ClickableViewAccessibility", "PrivateResource")
    fun bind(exerciseItemResponse: ExerciseModel, onItemSelected: (ExerciseModel) -> Unit) {

        binding.root.setOnClickListener{
            onItemSelected(exerciseItemResponse)
        }

            binding.tvExerciseName.text = exerciseItemResponse.getExerciseNameFromKey(binding.root.context)

    }
}
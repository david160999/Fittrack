package com.cursointermedio.myapplication.ui.addExercise.adapterExercise

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.ItemAddexerciseBinding
import com.cursointermedio.myapplication.databinding.ItemSelectedexerciseBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey


class SelectedExerciseViewHolder(private val binding: ItemSelectedexerciseBinding,
) :
    RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("ClickableViewAccessibility", "PrivateResource")
    fun bind(exerciseItemResponse: ExerciseModel, onItemSelected: (ExerciseModel) -> Unit) {
        binding.tvExerciseName.text = exerciseItemResponse.getExerciseNameFromKey(binding.root.context)

    }
}
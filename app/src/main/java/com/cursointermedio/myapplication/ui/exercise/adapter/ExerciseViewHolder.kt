package com.cursointermedio.myapplication.ui.exercise.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.MotionEvent
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.alpha
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.ExerciseDetailsCount
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.databinding.FragmentRealBinding
import com.cursointermedio.myapplication.databinding.ItemDetailsBinding
import com.cursointermedio.myapplication.databinding.ItemExerciseBinding
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.databinding.ItemWeekBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey

class ExerciseViewHolder(
    private val binding: ItemExerciseBinding
) : RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("ClickableViewAccessibility", "PrivateResource")
    fun bind(exerciseItemResponse: ExerciseModel, detailsCount: ExerciseDetailsCount, onItemSelected: (Long) -> Unit) {
        binding.tvTitle.text = exerciseItemResponse.getExerciseNameFromKey(binding.root.context)
        binding.tvNumTrainings.text = detailsCount.detailsCount.toString()

        binding.viewContainerTraining.setOnClickListener {
            onItemSelected(exerciseItemResponse.id!!)
        }


    }

    @SuppressLint("ClickableViewAccessibility", "PrivateResource", "Range")
    private fun CardView.setOnTouchListener(binding: ItemExerciseBinding) {


    }


}


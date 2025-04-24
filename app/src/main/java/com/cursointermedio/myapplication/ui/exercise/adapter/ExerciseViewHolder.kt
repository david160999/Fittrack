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
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.databinding.FragmentRealBinding
import com.cursointermedio.myapplication.databinding.ItemDetailsBinding
import com.cursointermedio.myapplication.databinding.ItemExerciseBinding
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.databinding.ItemWeekBinding

class ExerciseViewHolder(
    private val binding: ItemExerciseBinding
) : RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("ClickableViewAccessibility", "PrivateResource")
    fun bind(exerciseItemResponse: ExerciseEntity, onItemSelected: (Int) -> Unit) {
        binding.viewContainerTraining.setOnTouchListener(binding)


    }

    @SuppressLint("ClickableViewAccessibility", "PrivateResource", "Range")
    private fun CardView.setOnTouchListener(binding: ItemExerciseBinding) {


    }


}


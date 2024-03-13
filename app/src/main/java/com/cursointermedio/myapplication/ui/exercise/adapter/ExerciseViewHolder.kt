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
import com.cursointermedio.myapplication.databinding.FragmentRealBinding
import com.cursointermedio.myapplication.databinding.ItemDetailsBinding
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding

class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemDetailsBinding.bind(view)

    @SuppressLint("ClickableViewAccessibility", "PrivateResource")
    fun bind(exerciseItemResponse: String, onItemSelected: (Int) -> Unit) {
        binding.cv1.setOnTouchListener(binding)
    }

    @SuppressLint("ClickableViewAccessibility", "PrivateResource", "Range")
    private fun CardView.setOnTouchListener(binding: ItemDetailsBinding) {
        binding.cv1.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cv1.alpha = 0.2F
                    binding.ivCardView1.alpha = 0.8F
                }

                MotionEvent.ACTION_MOVE -> {}
                MotionEvent.ACTION_UP -> {
                    binding.cv1.alpha = 1F
                    binding.ivCardView1.alpha = 1F
                }

                MotionEvent.ACTION_CANCEL -> {
                    binding.cv1.alpha = 1F
                    binding.ivCardView1.alpha = 1F
                }
            }
            true
        }


    }


}


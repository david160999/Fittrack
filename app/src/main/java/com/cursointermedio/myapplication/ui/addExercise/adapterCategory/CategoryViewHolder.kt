package com.cursointermedio.myapplication.ui.addExercise.adapterCategory

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.databinding.ItemCategoryBinding
import com.cursointermedio.myapplication.databinding.ItemExerciseBinding
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewHolder(
    private val binding: ItemCategoryBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var isSelected = false

    fun defaultBg() {
        isSelected = false
        binding.btnCategory.setBackgroundColor(
            ContextCompat.getColor(
                binding.root.context,
                R.color.white
            )
        )
        binding.btnCategory.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                R.color.icons
            )
        )
    }

    fun selectedBg() {
        isSelected = true
        binding.btnCategory.setBackgroundColor(
            ContextCompat.getColor(
                binding.root.context,
                R.color.icons
            )
        )
        binding.btnCategory.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                R.color.white
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility", "PrivateResource")
    fun bind(
        exerciseItemResponse: CategoryInfo,
        onItemSelected: suspend (Long, Boolean) -> Unit
    ) {
        binding.btnCategory.text =
            ContextCompat.getString(binding.root.context, exerciseItemResponse.name)

        binding.btnCategory.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.alpha = 0.2f
                MotionEvent.ACTION_UP -> {
                    v.alpha = 1f
                    val x = event.x
                    val y = event.y
                    if (x in 0f..v.width.toFloat() && y in 0f..v.height.toFloat()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            onItemSelected(exerciseItemResponse.id, isSelected)
                        }
                    }
                }

                MotionEvent.ACTION_CANCEL -> v.alpha = 1f
            }
            false // para que siga propagando el evento (y funcione el click)
        }


    }

    @SuppressLint("ClickableViewAccessibility", "PrivateResource", "Range")
    private fun CardView.setOnTouchListener(binding: ItemExerciseBinding) {


    }
}
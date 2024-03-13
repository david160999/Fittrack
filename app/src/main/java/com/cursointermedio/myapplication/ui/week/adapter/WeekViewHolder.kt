package com.cursointermedio.myapplication.ui.week.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding

class WeekViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemTrainingBinding.bind(view)

    fun bind(trainingItemResponse: String, onItemSelected: (Int) -> Unit){
        binding.tvTitle.text = "Tamos weekend"
        binding.tvNumCurrentsWeeks.text = "qqq"

        binding.root.setOnClickListener{
            onItemSelected(0)
        }
    }

}
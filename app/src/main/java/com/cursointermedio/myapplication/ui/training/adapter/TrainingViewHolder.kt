package com.cursointermedio.myapplication.ui.training.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding

class TrainingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemTrainingBinding.bind(view)


    fun bind(trainingItemResponse: String, onItemSelected: (Int) -> Unit){
        binding.root.setOnClickListener{
            onItemSelected(0)
        }

    }
}
package com.cursointermedio.myapplication.ui.training.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.domain.model.TrainingModel

class TrainingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemTrainingBinding.bind(view)


    fun bind(trainingItemResponse: TrainingModel, onItemSelected: (Int) -> Unit){
        binding.tvTitle.text = trainingItemResponse.name.toString()

        binding.root.setOnClickListener{
            onItemSelected(0)
        }

    }
}
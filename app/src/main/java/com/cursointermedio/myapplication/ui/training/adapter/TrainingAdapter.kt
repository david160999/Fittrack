package com.cursointermedio.myapplication.ui.training.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R

class TrainingAdapter(
    private var trainingList: List<String> = emptyList(),
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<TrainingViewHolder>() {

    fun updateList(trainingList: List<String>) {
        this.trainingList = trainingList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        return TrainingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_training, parent, false)
        )
    }


    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        holder.bind(trainingList[position], onItemSelected)
    }

    override fun getItemCount() = trainingList.size
}


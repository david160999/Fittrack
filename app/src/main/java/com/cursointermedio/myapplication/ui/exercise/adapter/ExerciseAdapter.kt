package com.cursointermedio.myapplication.ui.exercise.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.ui.training.adapter.TrainingViewHolder

class ExerciseAdapter(
    private var exerciseList: List<String> = emptyList(),
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<ExerciseViewHolder>() {

    fun updateList(exerciseList: List<String>) {
        this.exerciseList = exerciseList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        return ExerciseViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_details, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exerciseList[position], onItemSelected)
    }

    override fun getItemCount() = exerciseList.size
}
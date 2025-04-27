package com.cursointermedio.myapplication.ui.exercise.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.ExerciseDetailsCount
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.databinding.ItemAddexerciseBinding
import com.cursointermedio.myapplication.databinding.ItemExerciseBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.ui.addExercise.adapterExercise.AddExerciseViewHolder
import com.cursointermedio.myapplication.ui.training.adapter.TrainingViewHolder

class ExerciseAdapter(
    private var exerciseList: List<ExerciseModel> = emptyList(),
    private val onItemSelected: (Long) -> Unit,
    private val detailsCountList: List<ExerciseDetailsCount>
) : RecyclerView.Adapter<ExerciseViewHolder>() {

    fun updateList(exerciseList: List<ExerciseModel>) {
        this.exerciseList = exerciseList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exerciseList[position], detailsCountList[position], onItemSelected)
    }

    override fun getItemCount() = exerciseList.size
}
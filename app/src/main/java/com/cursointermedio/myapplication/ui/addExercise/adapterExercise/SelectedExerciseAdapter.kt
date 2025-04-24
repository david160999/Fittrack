package com.cursointermedio.myapplication.ui.addExercise.adapterExercise

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.ItemSelectedexerciseBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel

class SelectedExerciseAdapter(
    private val onItemSelected: (ExerciseModel)-> Unit,
    private var exercises: List<ExerciseModel>
) :
    RecyclerView.Adapter<SelectedExerciseViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedExerciseViewHolder {
        val binding =
            ItemSelectedexerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectedExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectedExerciseViewHolder, position: Int) {
        holder.bind(exercises[position], onItemSelected)

    }

    override fun getItemCount() = exercises.size


    fun updateList(exerciseList: List<ExerciseModel>) {
        this.exercises = exerciseList
        notifyDataSetChanged()
    }

}


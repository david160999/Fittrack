package com.cursointermedio.myapplication.ui.addExercise.adapterExercise

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.databinding.ItemAddexerciseBinding
import com.cursointermedio.myapplication.databinding.ItemCategoryBinding
import com.cursointermedio.myapplication.databinding.ItemSelectedexerciseBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.ui.addExercise.adapterCategory.CategoryViewHolder


class AddExerciseAdapter(
    private val onItemSelected: (ExerciseModel) -> Unit,
    private var exercises: List<ExerciseModel>
) :
    RecyclerView.Adapter<AddExerciseViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddExerciseViewHolder {
        val binding =
            ItemAddexerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddExerciseViewHolder, position: Int) {
        holder.bind(exercises[position], onItemSelected)

    }

    override fun getItemCount() = exercises.size


    fun updateList(exerciseList: List<ExerciseModel>) {
        this.exercises = exerciseList
        notifyDataSetChanged()
    }

}


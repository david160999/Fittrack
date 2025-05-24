package com.cursointermedio.myapplication.ui.routine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey
import java.util.Collections

class DragExerciseAdapter(
    private val context: Context
) :
    ListAdapter<ExerciseModel, DragExerciseAdapter.ExerciseViewHolder>(ExerciseDiffCallback()) {

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tvNameDragAdapter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drag_routine, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.textView.text = getItem(position).getExerciseNameFromKey(context = context)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val currentListCopy = currentList.toMutableList()
        Collections.swap(currentListCopy, fromPosition, toPosition)
        submitList(currentListCopy)
    }

    class ExerciseDiffCallback : DiffUtil.ItemCallback<ExerciseModel>() {
        override fun areItemsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            return oldItem.id == newItem.id // Asegurate que `id` identifica unívocamente cada item
        }

        override fun areContentsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            return oldItem == newItem
        }
    }
}
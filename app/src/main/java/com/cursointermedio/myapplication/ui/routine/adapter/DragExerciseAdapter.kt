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
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey
import java.util.Collections

// Adaptador para una lista de ejercicios con soporte para arrastrar y reordenar ítems en un RecyclerView.
class DragExerciseAdapter(
    private val context: Context
) : ListAdapter<ExerciseModel, DragExerciseAdapter.ExerciseViewHolder>(ExerciseDiffCallback()) {

    // ViewHolder para el ítem de ejercicio
    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tvNameDragAdapter)
    }

    // Infla la vista del ítem y crea el ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drag_routine, parent, false)
        return ExerciseViewHolder(view)
    }

    // Asigna el nombre del ejercicio al TextView
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.textView.text = getItem(position).getExerciseNameFromKey(context = context)
    }

    // Mueve un ítem de una posición a otra en la lista y actualiza el adaptador
    fun moveItem(fromPosition: Int, toPosition: Int) {
        val currentListCopy = currentList.toMutableList()
        Collections.swap(currentListCopy, fromPosition, toPosition)
        submitList(currentListCopy)
    }

    // Callback para optimizar el refresco del RecyclerView
    class ExerciseDiffCallback : DiffUtil.ItemCallback<ExerciseModel>() {
        // Compara si dos ítems representan el mismo ejercicio (por ID único)
        override fun areItemsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            return oldItem.id == newItem.id
        }

        // Compara si los contenidos de los ítems son iguales
        override fun areContentsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            return oldItem == newItem
        }
    }
}
package com.cursointermedio.myapplication.ui.addExercise.adapterCategory

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.databinding.ItemCategoryBinding
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.ui.exercise.adapter.ExerciseViewHolder
import com.cursointermedio.myapplication.ui.routine.adapter.RoutineViewHolder

class CategoryAdapter(
    private val onItemSelected: suspend (Long, Boolean) -> Unit
) : ListAdapter<CategoryInfo, CategoryViewHolder>(CategoryDiffCallback()) {

    private var selectedItemPos = RecyclerView.NO_POSITION
    private var lastItemSelectedPos = RecyclerView.NO_POSITION
    private var isSelected = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val category = getItem(position)


        // Actualiza el fondo dependiendo del estado de selección
        when {
            isSelected -> {
                // Si algún ítem está seleccionado, mostrar el fondo por defecto
                holder.defaultBg()
                lastItemSelectedPos = RecyclerView.NO_POSITION
            }
            position == selectedItemPos -> {
                // Si es el ítem seleccionado, cambiar al fondo seleccionado
                holder.selectedBg()
            }
            else -> {
                // Ítems no seleccionados mantienen fondo por defecto
                holder.defaultBg()
            }
        }

        // Vincula los datos del ítem y el callback para la selección
        holder.bind(category, onItemSelected)

        // Maneja el click en el ítem
        holder.itemView.setOnClickListener {
            selectedItemPos = position

            // Cambia el estado de selección si se hace click sobre el mismo ítem
            isSelected = selectedItemPos == lastItemSelectedPos

            // Si no hay ítem previamente seleccionado, lo asigna
            if (lastItemSelectedPos == RecyclerView.NO_POSITION) {
                lastItemSelectedPos = selectedItemPos
            } else {
                // Notifica el cambio para actualizar el ítem previamente seleccionado
                notifyItemChanged(lastItemSelectedPos)
                lastItemSelectedPos = selectedItemPos
            }
            // Notifica que el ítem actual cambió para actualizar la UI
            notifyItemChanged(selectedItemPos)
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryInfo>() {
        override fun areItemsTheSame(oldItem: CategoryInfo, newItem: CategoryInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryInfo, newItem: CategoryInfo): Boolean {
            return oldItem == newItem
        }
    }
}
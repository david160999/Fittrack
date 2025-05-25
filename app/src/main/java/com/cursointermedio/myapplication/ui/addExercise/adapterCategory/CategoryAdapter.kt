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

        // Actualizar el fondo según el estado de selección
        when {
            isSelected -> {
                holder.defaultBg()
                lastItemSelectedPos = RecyclerView.NO_POSITION
            }
            position == selectedItemPos -> {
                holder.selectedBg()
            }
            else -> {
                holder.defaultBg()
            }
        }

        holder.bind(category, onItemSelected)

        holder.itemView.setOnClickListener {
            val oldSelectedPos = selectedItemPos
            selectedItemPos = position

            // Cambiar estado de selección
            isSelected = selectedItemPos == lastItemSelectedPos

            if (lastItemSelectedPos == RecyclerView.NO_POSITION) {
                lastItemSelectedPos = selectedItemPos
            } else {
                notifyItemChanged(lastItemSelectedPos)
                lastItemSelectedPos = selectedItemPos
            }
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
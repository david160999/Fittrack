package com.cursointermedio.myapplication.ui.addExercise.adapterCategory

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.databinding.ItemCategoryBinding
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.ui.exercise.adapter.ExerciseViewHolder
import com.cursointermedio.myapplication.ui.routine.adapter.RoutineViewHolder

class CategoryAdapter(
    private val onItemSelected: suspend (Long, Boolean) -> Unit,
    private var categories: List<CategoryInfo>
) :
    RecyclerView.Adapter<CategoryViewHolder>() {
    private var selectedItemPos = RecyclerView.NO_POSITION
    private var lastItemSelectedPos = RecyclerView.NO_POSITION
    private var isSelected = false

    fun updateList(categories: List<CategoryInfo>) {
        this.categories = categories
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        if (isSelected) {
            holder.defaultBg()
            lastItemSelectedPos = RecyclerView.NO_POSITION
        } else if (position == selectedItemPos) {
            holder.selectedBg()
        } else
            holder.defaultBg()

        holder.bind(categories[position], onItemSelected)


        holder.itemView.setOnClickListener {
            selectedItemPos = position

            isSelected = selectedItemPos == lastItemSelectedPos

            if (lastItemSelectedPos == -1)
                lastItemSelectedPos = selectedItemPos
            else {
                notifyItemChanged(lastItemSelectedPos)
                lastItemSelectedPos = selectedItemPos
            }
            notifyItemChanged(selectedItemPos)
        }


    }


}
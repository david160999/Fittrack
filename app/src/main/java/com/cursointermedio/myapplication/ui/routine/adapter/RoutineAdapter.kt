package com.cursointermedio.myapplication.ui.routine.adapter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.ui.training.adapter.TrainingMenuActions
import com.cursointermedio.myapplication.ui.training.adapter.TrainingViewHolder
import com.cursointermedio.myapplication.ui.week.adapter.WeekViewHolder

class RoutineAdapter(
    private val onItemSelected: (Long) -> Unit,
    private val menuActions: RoutineMenuActions
) : ListAdapter<RoutineModel, RoutineViewHolder>(RoutineDiffCallback()) {

    class RoutineDiffCallback : DiffUtil.ItemCallback<RoutineModel>() {
        override fun areItemsTheSame(oldItem: RoutineModel, newItem: RoutineModel): Boolean {
            // Aquí defines cuándo dos items son el mismo, típicamente por ID
            return oldItem.routineId == newItem.routineId
        }

        override fun areContentsTheSame(oldItem: RoutineModel, newItem: RoutineModel): Boolean {
            // Aquí defines si el contenido de los items es el mismo
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val binding = ItemTrainingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoutineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = getItem(position)
        holder.bind(routine, onItemSelected, menuActions)


    }
}

class MarginItemDecoration(
    private val defaultMargin: Int,
    private val lastItemMargin: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        // Asigna el margen inferior según la posición del elemento
        if (position == itemCount - 1) {
            outRect.bottom = lastItemMargin
        }else{
            outRect.bottom = defaultMargin
        }
    }
}
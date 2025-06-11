package com.cursointermedio.myapplication.ui.routine.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.domain.model.RoutineModel

// Adaptador para la lista de rutinas, usando ListAdapter para eficiencia en cambios
class RoutineAdapter(
    private val onItemSelected: (Long) -> Unit,       // Callback cuando se selecciona una rutina (por ID)
    private val menuActions: RoutineMenuActions        // Acciones asociadas al menú contextual de rutina
) : ListAdapter<RoutineModel, RoutineViewHolder>(RoutineDiffCallback()) {

    class RoutineDiffCallback : DiffUtil.ItemCallback<RoutineModel>() {
        override fun areItemsTheSame(oldItem: RoutineModel, newItem: RoutineModel): Boolean {
            return oldItem.routineId == newItem.routineId
        }

        override fun areContentsTheSame(oldItem: RoutineModel, newItem: RoutineModel): Boolean {
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

// Decoración para separar elementos en un RecyclerView con márgenes inferiores variables
class MarginItemDecoration(
    private val defaultMargin: Int,    // Margen para todos los elementos excepto el último
    private val lastItemMargin: Int    // Margen especial para el último elemento
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        // Si es el último elemento, aplica el margen especial, si no, el margen por defecto
        if (position == itemCount - 1) {
            outRect.bottom = lastItemMargin
        } else {
            outRect.bottom = defaultMargin
        }
    }
}
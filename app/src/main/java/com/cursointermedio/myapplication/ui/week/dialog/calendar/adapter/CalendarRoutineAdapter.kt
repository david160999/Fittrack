package com.cursointermedio.myapplication.ui.week.dialog.calendar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.ItemCalendarRoutineBinding
import com.cursointermedio.myapplication.domain.model.RoutineModel

// Adaptador para seleccionar una rutina en un calendario. Permite seleccionar/deseleccionar visualmente una rutina con feedback al callback.

class CalendarRoutineAdapter(
    private val onItemSelected: (Int?) -> Unit,
) : ListAdapter<RoutineModel, CalendarRoutineViewHolder>(CalendarRoutineDiffCallback()) {

    private var selectedItemPos = RecyclerView.NO_POSITION
    private var lastItemSelectedPos = RecyclerView.NO_POSITION
    private var isSelected = false
    private var firstTime = true

    // DiffUtil para optimizar el refresco de la lista
    class CalendarRoutineDiffCallback : DiffUtil.ItemCallback<RoutineModel>() {
        override fun areItemsTheSame(
            oldItem: RoutineModel,
            newItem: RoutineModel
        ): Boolean = oldItem.routineId == newItem.routineId

        override fun areContentsTheSame(
            oldItem: RoutineModel,
            newItem: RoutineModel
        ): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarRoutineViewHolder {
        val binding = ItemCalendarRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarRoutineViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CalendarRoutineViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = getItem(position)

        // Lógica de selección visual y callback
        if (firstTime && position == 0) {
            holder.selectedBg()
            firstTime = false
            selectedItemPos = 0
            lastItemSelectedPos = 0
            onItemSelected(0)
        } else if (isSelected) {
            holder.defaultBg()
            lastItemSelectedPos = RecyclerView.NO_POSITION
            onItemSelected(null)
        } else if (position == selectedItemPos) {
            holder.selectedBg()
            onItemSelected(position)
        } else {
            holder.defaultBg()
        }

        holder.bind(item)

        holder.itemView.setOnClickListener {
            // Si se hace click en el seleccionado, deselecciona
            isSelected = selectedItemPos == position
            if (isSelected) {
                val prevSelected = selectedItemPos
                selectedItemPos = RecyclerView.NO_POSITION
                notifyItemChanged(prevSelected)
                onItemSelected(null)
            } else {
                val prevSelected = selectedItemPos
                selectedItemPos = position
                notifyItemChanged(prevSelected)
                notifyItemChanged(selectedItemPos)
                onItemSelected(position)
            }
            lastItemSelectedPos = selectedItemPos
        }
    }
}
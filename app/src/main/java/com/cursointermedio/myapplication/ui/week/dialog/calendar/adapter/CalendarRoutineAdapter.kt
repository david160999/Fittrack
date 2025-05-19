package com.cursointermedio.myapplication.ui.week.dialog.calendar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.databinding.ItemCalendarRoutineBinding
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.ui.training.adapter.TrainingMenuActions
import com.cursointermedio.myapplication.ui.training.adapter.TrainingViewHolder

class CalendarRoutineAdapter(
    private val onItemSelected: (Int?) -> Unit,

    ) : ListAdapter<RoutineModel, CalendarRoutineViewHolder>(CalendarRoutineDiffCallback()) {

    private var selectedItemPos = RecyclerView.NO_POSITION
    private var lastItemSelectedPos = RecyclerView.NO_POSITION
    private var isSelected = false
    private var firstTime = true


    class CalendarRoutineDiffCallback : DiffUtil.ItemCallback<RoutineModel>() {
        override fun areItemsTheSame(
            oldItem: RoutineModel,
            newItem: RoutineModel
        ): Boolean {
            return oldItem.routineId == newItem.routineId
        }

        override fun areContentsTheSame(
            oldItem: RoutineModel,
            newItem: RoutineModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarRoutineViewHolder {
        val binding =
            ItemCalendarRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarRoutineViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: CalendarRoutineViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = getItem(position)

        if (firstTime && position == 0) {
            holder.selectedBg()
            firstTime = false
            selectedItemPos = 0
            lastItemSelectedPos = 0
        } else if (isSelected) {
            holder.defaultBg()
            lastItemSelectedPos = RecyclerView.NO_POSITION
            onItemSelected(null)
        } else if (position == selectedItemPos) {
            holder.selectedBg()
            onItemSelected(position)
        } else
            holder.defaultBg()

        holder.bind(item)

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


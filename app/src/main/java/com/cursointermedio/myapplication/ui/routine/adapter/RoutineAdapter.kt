package com.cursointermedio.myapplication.ui.routine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.ui.training.adapter.TrainingViewHolder
import com.cursointermedio.myapplication.ui.week.adapter.WeekViewHolder

class RoutineAdapter(
    private val onItemSelected: (Long) -> Unit,
    private var routines: List<RoutineEntity>
) :
    RecyclerView.Adapter<RoutineViewHolder>() {

    fun updateList(routineList: List<RoutineEntity>) {
        this.routines = routineList
        notifyDataSetChanged()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val binding = ItemTrainingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoutineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = routines[position]
        holder.bind(routine, onItemSelected)
    }

    override fun getItemCount(): Int = routines.size

}
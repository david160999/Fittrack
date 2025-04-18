package com.cursointermedio.myapplication.ui.routine.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding

class RoutineViewHolder(private val binding: ItemTrainingBinding,
    ) :
    RecyclerView.ViewHolder(binding.root) {
    private lateinit var numWeeks: String
    private lateinit var numRoutines: String
    private var routineId: Long = 0

    fun bind(routine: RoutineEntity, onItemSelected: (Long) -> Unit) {
        binding.tvTitle.text = routine.name

        binding.root.setOnClickListener {
            this.routineId = routine.routineId!!
            onItemSelected(routineId)
        }
    }


}
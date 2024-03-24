package com.cursointermedio.myapplication.ui.week.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding

class WeekViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemTrainingBinding.bind(view)

    fun bind(routineItemResponse: RoutineEntity, onItemSelected: (Int) -> Unit){
        val nameWeek = routineItemResponse.name
        binding.tvTitle.text = nameWeek

        binding.root.setOnClickListener{
            onItemSelected(0)
        }
    }

}
package com.cursointermedio.myapplication.ui.routine.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.ui.training.adapter.TrainingViewHolder

class RoutineAdapter(
    private var routineList: List<String> = emptyList(),
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<RoutineViewHolder>() {

    fun updateList(routineList: List<String>) {
        this.routineList = routineList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        return RoutineViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_training, parent, false)
        )
    }


    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        holder.bind(routineList[position], onItemSelected)
    }

    override fun getItemCount() = routineList.size
}
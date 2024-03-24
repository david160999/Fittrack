package com.cursointermedio.myapplication.ui.week.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines

class WeekAdapter(
    private var listWeekWithRoutines: List<WeekWithRoutines> = emptyList(),
    private val onItemSelected: (Int) -> Unit,
    private val weekId : Int
) : RecyclerView.Adapter<WeekViewHolder>() {

    fun updateList(weekList: List<WeekWithRoutines>) {
        this.listWeekWithRoutines = weekList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        return WeekViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_training, parent, false)
        )
    }


    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        holder.bind(listWeekWithRoutines[weekId].routineList[position], onItemSelected)
    }

    override fun getItemCount() = listWeekWithRoutines.size
}

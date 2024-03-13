package com.cursointermedio.myapplication.ui.week.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.ui.training.adapter.TrainingViewHolder

class WeekAdapter(
    private var weekList: List<String> = emptyList(),
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<WeekViewHolder>() {

    fun updateList(weekList: List<String>) {
        this.weekList = weekList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        return WeekViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_training, parent, false)
        )
    }


    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        holder.bind(weekList[position], onItemSelected)
    }

    override fun getItemCount() = weekList.size
}

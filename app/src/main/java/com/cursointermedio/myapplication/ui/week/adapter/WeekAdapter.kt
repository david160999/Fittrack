package com.cursointermedio.myapplication.ui.week.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.FragmentWeekBinding
import com.cursointermedio.myapplication.databinding.ItemWeekBinding
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel

class WeekAdapter(
    private val onItemSelected: (Long) -> Unit,
    private val binding: FragmentWeekBinding
) :
    RecyclerView.Adapter<WeekViewHolder>() {
    private var weeks: List<WeekWithRoutinesModel> = mutableListOf()

    fun updateList(weekList: List<WeekWithRoutinesModel>) {
        this.weeks = weekList
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {

        val binding = ItemWeekBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeekViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val week = weeks[position]
        holder.bind(week, onItemSelected, weeks, binding)
    }

    override fun getItemCount(): Int = weeks.size

}
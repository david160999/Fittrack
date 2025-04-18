package com.cursointermedio.myapplication.ui.week.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.databinding.FragmentWeekBinding
import com.cursointermedio.myapplication.databinding.ItemWeekBinding
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.ui.routine.adapter.RoutineAdapter

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
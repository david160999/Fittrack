package com.cursointermedio.myapplication.ui.week.adapter

import android.R
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.databinding.FragmentWeekBinding
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.databinding.ItemWeekBinding
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.ui.routine.adapter.RoutineAdapter
import com.cursointermedio.myapplication.ui.training.adapter.TrainingAdapter
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch

class WeekViewHolder(
    private val binding: ItemWeekBinding, private val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    private var currentRoutines: List<RoutineEntity> =
        emptyList() // List of routines for the selected week
    private lateinit var adapter: RoutineAdapter


    fun bind(
        week: WeekWithRoutinesModel,
        onItemSelected: (Long) -> Unit,
        weeks: List<WeekWithRoutinesModel>,
        bindWeek: FragmentWeekBinding
    ) {
        // Set routines for the selected week

    }
}


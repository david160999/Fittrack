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
        currentRoutines = week.routineList


        adapter = RoutineAdapter(
            onItemSelected = { trainingId ->
                onItemSelected(trainingId)
            }, this.currentRoutines
        )

        binding.rvRoutines.layoutManager = LinearLayoutManager(context)
        binding.rvRoutines.adapter = adapter


        // Set up the spinner with week names
        val spinnerAdapter = ArrayAdapter(context,
            R.layout.simple_spinner_dropdown_item,
            List(weeks.size) { index -> "Semana ${index + 1}" })

        //bindWeek.dropMenu.adapter = spinnerAdapter
        //binding.spinnerWeeks.adapter = spinnerAdapter
        // Update routines when a different week is selected
        binding.spinnerWeeks.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val selectedWeek = weeks[position]
                // Update the routines based on the selected week
                currentRoutines = selectedWeek.routineList
                binding.rvRoutines.adapter = RoutineAdapter(onItemSelected = { trainingId ->
                    onItemSelected(trainingId)
                }, currentRoutines)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}


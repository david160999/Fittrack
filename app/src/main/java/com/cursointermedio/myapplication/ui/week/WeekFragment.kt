package com.cursointermedio.myapplication.ui.week

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.common.ktx.R
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.data.database.entities.RoutineEntity
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.WeekEntity
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.databinding.FragmentWeekBinding
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.ui.routine.adapter.RoutineAdapter
import com.cursointermedio.myapplication.ui.routine.dialog.RoutineDialog
import com.cursointermedio.myapplication.ui.training.CurrentFeature.*
import com.cursointermedio.myapplication.ui.training.CurrentFeature.TypeFeature.*
import com.cursointermedio.myapplication.ui.training.dialog.TrainingDialog
import com.cursointermedio.myapplication.ui.week.adapter.WeekAdapter
import com.cursointermedio.myapplication.ui.week.dialog.WeekDialog
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@AndroidEntryPoint
class WeekFragment : Fragment() {

    private val currentFeature = Feature

    private val weekViewModel: WeekViewModel by viewModels()

    private var _binding: FragmentWeekBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RoutineAdapter
    private lateinit var listWeekWithRoutines: StateFlow<List<WeekWithRoutinesModel>>
    private lateinit var sizeListWeek: String

    private lateinit var adapterWeeks: ArrayAdapter<WeekWithRoutines>

    private val args: WeekFragmentArgs by navArgs()
    private var trainingId: Long = 0
    private var weekNum: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentFeature.setFeature(WeekFeature)
        trainingId = args.id.toLong()
        initUI()
        initListener()

    }

    private fun initListener() {
        binding.ivPlusWeek.setOnTouchListener(binding)
    }

    private fun initUI() {
        listWeekWithRoutines = weekViewModel.getAllWeeksWithRoutines(trainingId)
        lifecycleScope.launch {
            listWeekWithRoutines.collect { weeks ->
                // Solo hacemos esto si hay datos
                if (weeks.isNotEmpty()) {
                    setupWeekSpinner(weeks)

                    val selected = getSelectedItemFromDropMenu()
                    val updatedRoutine = weeks.getOrNull(selected)?.routineList.orEmpty()

                    if (!::adapter.isInitialized) {
                        adapter = RoutineAdapter(
                            onItemSelected = { routineId ->
                                navigateToRoutine(routineId)
                            }, routines = updatedRoutine
                        )
                        binding.rvRoutine.layoutManager = LinearLayoutManager(context)
                        binding.rvRoutine.adapter = adapter
                    } else {
                        adapter.updateList(updatedRoutine)
                    }
                }
            }
        }

    }

    private fun getSelectedItemFromDropMenu(): Int {
        val weeks = this.listWeekWithRoutines.value

        val weekTitles = List(weeks.size) { index -> "Semana ${index + 1}" }

        val textoSeleccionado = binding.dropMenu.text.toString()
        val selectedPosition = weekTitles.indexOf(textoSeleccionado)

        return selectedPosition
    }

    private fun setupWeekSpinner(weeks: List<WeekWithRoutinesModel>) {
        val weekTitles = List(weeks.size) { index -> "Semana ${index + 1}" }

        val spinnerAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, weekTitles
        )

        val dropMenu = binding.dropMenu
        dropMenu.setAdapter(spinnerAdapter)

        val itemCount = dropMenu.adapter?.count ?: 0
        if (itemCount > 0) {
            val item = dropMenu.adapter.getItem(itemCount - 1)
            dropMenu.setText(item.toString(), false)
        }

        binding.dropMenu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val selectedWeek = weeks[position]
                adapter.updateList(selectedWeek.routineList)
                updateRoutineList(selectedWeek.routineList)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateRoutineList(routines: List<RoutineEntity>) {
        binding.rvRoutine.adapter = RoutineAdapter(onItemSelected = { routineId ->
            navigateToRoutine(routineId)
        }, routines = routines)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun ImageView.setOnTouchListener(binding: FragmentWeekBinding) {
        binding.ivPlusWeek.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    binding.ivPlusWeek.alpha = 0.2F
                }

                android.view.MotionEvent.ACTION_MOVE -> {}
                android.view.MotionEvent.ACTION_UP -> {
                    binding.ivPlusWeek.alpha = 1F
                    createDialog()

                }

                android.view.MotionEvent.ACTION_CANCEL -> {
                    binding.ivPlusWeek.alpha = 1F
                }
            }
            true
        }
        binding.ivPlus.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    binding.ivPlusWeek.alpha = 0.2F
                }

                android.view.MotionEvent.ACTION_MOVE -> {}
                android.view.MotionEvent.ACTION_UP -> {
                    binding.ivPlusWeek.alpha = 1F
                    createRoutineDialog()

                }

                android.view.MotionEvent.ACTION_CANCEL -> {
                    binding.ivPlusWeek.alpha = 1F
                }
            }
            true
        }
    }

    private fun createDialog() {

        val selected = getSelectedItemFromDropMenu()
        val weekId = listWeekWithRoutines.value.getOrNull(selected)?.week?.weekId


        val dialog = WeekDialog(onSaveClickListener = { option ->
            lifecycleScope.launch {
                when (option) {
                    "CopyWeek", "CopyWeekWithObj", "CopyWeekWithAll" -> weekViewModel.createCopyOfWeek(
                        weekId, trainingId, option,
                    )

                }
            }
        })

        dialog.show(parentFragmentManager, "dialog")
    }

    private fun createRoutineDialog() {
        val selected = getSelectedItemFromDropMenu()
        val weekId = listWeekWithRoutines.value[selected].week.weekId

        if (weekId != null) {
            val dialog = RoutineDialog(onSaveClickListener = {
                lifecycleScope.launch {
                    val routine = RoutineModel(null, weekId, null, null)
                    weekViewModel.insertRoutine(routine)
                }
            }, weekId)
            dialog.show(parentFragmentManager, "dialog")
        }
    }

    private fun navigateToRoutine(routineId: Long) {
        findNavController().navigate(
            WeekFragmentDirections.actionWeekFragmentToRoutineFragment(
                routineId.toInt()
            )
        )
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeekBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

}



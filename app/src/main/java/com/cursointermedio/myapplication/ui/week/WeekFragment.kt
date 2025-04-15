package com.cursointermedio.myapplication.ui.week

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.databinding.FragmentWeekBinding
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.domain.model.WeekWithRoutinesModel
import com.cursointermedio.myapplication.ui.training.CurrentFeature.*
import com.cursointermedio.myapplication.ui.training.CurrentFeature.TypeFeature.*
import com.cursointermedio.myapplication.ui.training.dialog.TrainingDialog
import com.cursointermedio.myapplication.ui.week.adapter.WeekAdapter
import com.cursointermedio.myapplication.ui.week.dialog.WeekDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeekFragment : Fragment() {

    private val currentFeature = Feature

    private val weekViewModel: WeekViewModel by viewModels()

    private var _binding: FragmentWeekBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: WeekAdapter
    private lateinit var listWeekWithRoutines: Flow<List<WeekWithRoutinesModel>>
    private lateinit var sizeListWeek: String
    private var weekId: Long = 0


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

        adapter = WeekAdapter(
            onItemSelected = { routineId ->
                navigateToRoutine(routineId)
            },
            weekNum = weekNum
        )

        binding.rvRoutine.layoutManager = LinearLayoutManager(context)
        binding.rvRoutine.adapter = adapter

        lifecycleScope.launch {
            listWeekWithRoutines.collect {
                adapter.updateList(it)

                if (it.isNotEmpty() && weekNum < it.size) {
                    weekId = it[weekNum].week.weekId!!
                }
            }
        }

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
    }

    private  fun createDialog() {
        val dialog = WeekDialog(
            onSaveClickListener = { option ->
                lifecycleScope.launch {
                    when (option) {
                        "CopyWeek", "CopyWeekWithObj", "CopyWeekWithAll" -> weekViewModel.createCopyOfWeek(
                            weekId,
                            trainingId,
                            option
                        )
                    }
                }
            }
        )

        dialog.show(parentFragmentManager, "dialog")
    }

    private fun navigateToRoutine(routineId: Int) {
        findNavController().navigate(
            WeekFragmentDirections.actionWeekFragmentToRoutineFragment(
                routineId
            )
        )
    }

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeekBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

}



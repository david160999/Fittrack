package com.cursointermedio.myapplication.ui.routine

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.RoutineWithExercises
import com.cursointermedio.myapplication.databinding.FragmentRoutineBinding
import com.cursointermedio.myapplication.databinding.FragmentTrainingBinding
import com.cursointermedio.myapplication.databinding.FragmentWeekBinding
import com.cursointermedio.myapplication.ui.exercise.adapter.ExerciseAdapter
import com.cursointermedio.myapplication.ui.routine.adapter.RoutineAdapter
import com.cursointermedio.myapplication.ui.training.CurrentFeature
import com.cursointermedio.myapplication.ui.training.TrainingFragmentDirections
import com.cursointermedio.myapplication.ui.training.TrainingViewModel
import com.cursointermedio.myapplication.ui.training.adapter.TrainingAdapter
import com.cursointermedio.myapplication.ui.week.WeekFragmentArgs
import com.cursointermedio.myapplication.ui.week.WeekViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RoutineFragment : Fragment() {

    private val routineViewModel: RoutineViewModel by viewModels()


    private val currentFeature = CurrentFeature.Feature


    private var _binding: FragmentRoutineBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExerciseAdapter
    private lateinit var routine: RoutineWithExercises

    private val args: RoutineFragmentArgs by navArgs()
    private var routineId: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        routineId = args.id.toLong()
        currentFeature.setFeature(CurrentFeature.TypeFeature.RoutineFeature)
        initUI()
        initListener()
    }

    private fun initListener() {
        binding.ivPlusExercise.setOnTouchListener(binding)
    }

    private fun initUI() {
        lifecycleScope.launch {
            routine = routineViewModel.getRoutineWithExercises(routineId)

            if (::routine.isInitialized) {
                val name = routine.routine.name.toString()
                binding.tvTitle.text = name

                adapter = ExerciseAdapter { exerciseId -> navigateToExercise(exerciseId) }
                binding.rvExercise.layoutManager = LinearLayoutManager(context)
                binding.rvExercise.adapter = adapter
                adapter.updateList(routine.exercises)
            }
        }

    }

    private fun navigateToExercise(exerciseId: Int) {
        findNavController().navigate(
            RoutineFragmentDirections.actionRoutineFragmentToExerciseFragment(
                exerciseId
            )
        )
    }

    private fun navigateToAddExercise() {
        findNavController().navigate(
            RoutineFragmentDirections.actionRoutineFragmentToAddExerciseFragment(routineId)
        )
    }

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoutineBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun ImageView.setOnTouchListener(binding: FragmentRoutineBinding) {
        binding.ivPlusExercise.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    binding.ivPlusExercise.alpha = 0.2F
                }

                android.view.MotionEvent.ACTION_MOVE -> {}
                android.view.MotionEvent.ACTION_UP -> {
                    binding.ivPlusExercise.alpha = 1F
                    val x = event.x
                    val y = event.y
                    if (x >= 0 && x <= v.width && y >= 0 && y <= v.height) {
                        navigateToAddExercise()
                    }

                }

                android.view.MotionEvent.ACTION_CANCEL -> {
                    binding.ivPlusExercise.alpha = 1F
                }
            }
            true
        }
    }
}
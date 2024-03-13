package com.cursointermedio.myapplication.ui.routine

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentRoutineBinding
import com.cursointermedio.myapplication.databinding.FragmentTrainingBinding
import com.cursointermedio.myapplication.ui.routine.adapter.RoutineAdapter
import com.cursointermedio.myapplication.ui.training.CurrentFeature
import com.cursointermedio.myapplication.ui.training.TrainingFragmentDirections
import com.cursointermedio.myapplication.ui.training.TrainingViewModel
import com.cursointermedio.myapplication.ui.training.adapter.TrainingAdapter


class RoutineFragment : Fragment() {

    private val routineViewModel by viewModels<RoutineViewModel>()

    private val currentFeature = CurrentFeature.Feature


    private var _binding: FragmentRoutineBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RoutineAdapter
    private var listExercise = listOf("qqq", "baa", "qq")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        currentFeature.setFeature(CurrentFeature.TypeFeature.RoutineFeature)
    }

    private fun initUI() {
        adapter = RoutineAdapter{exerciseId -> navigateToExercise(exerciseId)}
        binding.rvExercise.layoutManager = LinearLayoutManager(context)
        binding.rvExercise.adapter = adapter
        adapter.updateList(listExercise)
    }

    private fun navigateToExercise(exerciseId: Int) {
        findNavController().navigate(RoutineFragmentDirections.actionRoutineFragmentToExerciseFragment(exerciseId))
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
}
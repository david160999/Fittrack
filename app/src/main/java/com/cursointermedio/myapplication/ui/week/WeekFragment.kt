package com.cursointermedio.myapplication.ui.week

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentTrainingBinding
import com.cursointermedio.myapplication.databinding.FragmentWeekBinding
import com.cursointermedio.myapplication.ui.training.CurrentFeature
import com.cursointermedio.myapplication.ui.training.CurrentFeature.*
import com.cursointermedio.myapplication.ui.training.CurrentFeature.TypeFeature.*
import com.cursointermedio.myapplication.ui.training.adapter.TrainingAdapter
import com.cursointermedio.myapplication.ui.week.adapter.WeekAdapter

class WeekFragment : Fragment() {

    private val currentFeature = Feature

    private var _binding: FragmentWeekBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: WeekAdapter
    private var listRoutine = listOf("qqq", "baa", "qq")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

        currentFeature.setFeature(WeekFeature)
    }

    private fun initUI() {
        adapter = WeekAdapter{routineId -> navigateToRoutine(routineId)}
        binding.rvRoutine.layoutManager = LinearLayoutManager(context)
        binding.rvRoutine.adapter = adapter
        adapter.updateList(listRoutine)
    }

    private fun navigateToRoutine(routineId: Int) {
        findNavController().navigate(WeekFragmentDirections.actionWeekFragmentToRoutineFragment(routineId))
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
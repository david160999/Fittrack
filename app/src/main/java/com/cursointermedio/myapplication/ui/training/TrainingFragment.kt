package com.cursointermedio.myapplication.ui.training

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentTrainingBinding
import com.cursointermedio.myapplication.ui.training.adapter.TrainingAdapter


class TrainingFragment : Fragment() {

    private val trainingViewModel by viewModels<TrainingViewModel>()

    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrainingAdapter

    private var listTraining = listOf("qqq", "baa", "qq")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun initUI() {
        adapter = TrainingAdapter{trainingId -> navigateToWeek(trainingId)}
        binding.rvTraining.layoutManager = LinearLayoutManager(context)
        binding.rvTraining.adapter = adapter
        adapter.updateList(listTraining)
    }

    private fun navigateToWeek(trainingId: Int) {
        findNavController().navigate(TrainingFragmentDirections.actionTrainingFragmentToWeekFragment(trainingId))
    }

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrainingBinding.inflate(
            inflater,
            container,
            false
        )
        initUI()

        val navController = findNavController()
        val feature = trainingViewModel.getFeature()

        if (feature!=null){
            navController.navigate(feature)
        }
        return binding.root
    }




}
package com.cursointermedio.myapplication.ui.training

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.databinding.FragmentTrainingBinding
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.ui.training.adapter.TrainingAdapter
import com.cursointermedio.myapplication.ui.training.dialog.TrainingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrainingFragment @Inject constructor() : Fragment() {

    private val trainingViewModel by viewModels<TrainingViewModel>()

    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrainingAdapter

    private var listTraining = flowOf<List<TrainingModel>>()
    private lateinit var sizeListTraining: String
    private lateinit var listTrainingWithWeeksAndRoutines: Flow<List<TrainingWithWeeksAndRoutines>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

    }

    private fun initUI() {
        initList()
        initListener()
    }

    private fun initList() {
        listTrainingWithWeeksAndRoutines = trainingViewModel.getTrainingWithWeeksAndRoutines()
        listTraining = trainingViewModel.getTrainingsFromDataBase()

        adapter = TrainingAdapter(
            onItemSelected = { trainingId ->
                navigateToWeek(trainingId)
            },
        )
        binding.rvTraining.layoutManager = LinearLayoutManager(context)
        binding.rvTraining.adapter = adapter

        lifecycleScope.launch {
            listTrainingWithWeeksAndRoutines.collect {
                adapter.updateList(it)
                sizeListTraining = it.size.toString()
            }
        }
    }

    private fun initListener() {
        binding.ivPlus.setOnTouchListener(binding)
    }

    private fun navigateToWeek(trainingId: Int) {
        findNavController().navigate(
            TrainingFragmentDirections.actionTrainingFragmentToWeekFragment(
                trainingId
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility", "PrivateResource")
    private fun ImageView.setOnTouchListener(binding: FragmentTrainingBinding) {
        binding.ivPlus.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.ivPlus.alpha = 0.2F
                }

                MotionEvent.ACTION_MOVE -> {}
                MotionEvent.ACTION_UP -> {
                    binding.ivPlus.alpha = 1F
                    createDialog()

                }

                MotionEvent.ACTION_CANCEL -> {
                    binding.ivPlus.alpha = 1F
                }
            }
            true
        }


    }

    private fun createDialog() {

        val dialog = TrainingDialog(
            onSaveClickListener = { name ->
                lifecycleScope.launch {
                    val training = TrainingModel(null, name, null)
                    trainingViewModel.insertTraining(training)
                }
            }, sizeListTraining
        )
        dialog.show(parentFragmentManager, "dialog")

    }


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrainingBinding.inflate(
            inflater, container, false
        )

        val navController = findNavController()
//        val feature = trainingViewModel.getFeature()

//        if (feature != null) {
//            navController.navigate(feature)
//        }
        return binding.root
    }


}
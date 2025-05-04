package com.cursointermedio.myapplication.ui.training

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.databinding.FragmentTrainingBinding
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.ui.training.adapter.TrainingAdapter
import com.cursointermedio.myapplication.ui.training.adapter.TrainingMenuActions
import com.cursointermedio.myapplication.ui.training.dialog.TrainingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class TrainingFragment @Inject constructor() : Fragment() {

    private val trainingViewModel by viewModels<TrainingViewModel>()

    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrainingAdapter

    private lateinit var sizeListTraining: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

    }

    private fun initUI() {
        initList()
        initListener()
    }

    private fun initList() {

        adapter = TrainingAdapter(
            onItemSelected = { trainingId ->
                navigateToWeek(trainingId)
            }, menuActions = TrainingMenuActions(
                onChangeName = { id->
                    lifecycleScope.launch { trainingViewModel.changeNameTraining(id) }
                },
                onCopy = { id ->
                    lifecycleScope.launch { trainingViewModel.copyTraining(id) }
                },
                onShare = { id ->
                    lifecycleScope.launch { trainingViewModel.shareTraining(id) }
                },
                onEliminate = { id ->
                    lifecycleScope.launch { trainingViewModel.deleteTraining(id) }
                }
            )
        )
        val layoutManager = LinearLayoutManager(context)
        binding.rvTraining.layoutManager = layoutManager
        binding.rvTraining.adapter = adapter

        lifecycleScope.launch{
            trainingViewModel.trainings.collectLatest { list ->
                adapter.submitList(list)
                sizeListTraining = list.size.toString()
            }
        }
    }

    private fun initListener() {
        binding.ivPlus.setupTouchAction {
            createDialog()
        }
        binding.ivDownload.setupTouchAction {

        }
    }

    private fun navigateToWeek(trainingId: Long) {
        findNavController().navigate(
            TrainingFragmentDirections.actionTrainingFragmentToWeekFragment(
                id = trainingId.toInt()
            )
        )
    }

    private fun createDialog() {

        val dialog = TrainingDialog(
            onSaveClickListener = { name ->
                lifecycleScope.launch {
                    val training = TrainingModel(null, name, null)
                    val trainingId = trainingViewModel.insertTraining(training)

                    val week = WeekModel(null, trainingId, null, null)
                    val weekId = trainingViewModel.insertWeek(week)

                    navigateToWeek(weekId)
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
        return binding.root
    }


}
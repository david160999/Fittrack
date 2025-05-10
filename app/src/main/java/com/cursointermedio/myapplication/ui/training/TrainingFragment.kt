package com.cursointermedio.myapplication.ui.training

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.databinding.FragmentTrainingBinding
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import com.cursointermedio.myapplication.ui.training.adapter.TrainingAdapter
import com.cursointermedio.myapplication.ui.training.adapter.TrainingMenuActions
import com.cursointermedio.myapplication.ui.training.dialog.TrainingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import kotlinx.coroutines.flow.collectLatest


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
                    trainingViewModel.changeNameTraining(id)
                },
                onCopy = { id ->
                    trainingViewModel.copyTraining(id)
                    binding.rvTraining.smoothScrollToPosition(0)

                },
                onShare = { id ->
                    trainingViewModel.uploadTrainingData(id)
                },
                onEliminate = { id ->
                    trainingViewModel.deleteTraining(id)
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

        trainingViewModel.trainingId.observe(viewLifecycleOwner, Observer { trainingId ->
            trainingId?.let {
                navigateToWeek(trainingId)
            }?: run {
                Toast.makeText(context, "Error al insertar el entrenamiento", Toast.LENGTH_SHORT).show()
            }
        })

        trainingViewModel.trainingHashCode.observe(viewLifecycleOwner, Observer { trainingHashCode ->
            trainingHashCode?.let {
            }?: run {
                Toast.makeText(context, "Error al intentar compartir el entramiento", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToWeek(trainingId: Long) {
        findNavController().navigate(
            TrainingFragmentDirections.actionTrainingFragmentToWeekFragment(
                id = trainingId
            )
        )
    }

    private fun createDialog() {
        val dialog = TrainingDialog(
            onSaveClickListener = { name ->
                    trainingViewModel.insertTrainingAndWeek(name)
            }, sizeListTraining
        )
        dialog.show(parentFragmentManager, "dialog")
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingBinding.inflate(
            inflater, container, false
        )
        val navController = findNavController()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpiar el binding cuando la vista se destruya
        _binding = null
    }

}
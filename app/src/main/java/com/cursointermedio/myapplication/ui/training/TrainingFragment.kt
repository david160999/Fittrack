package com.cursointermedio.myapplication.ui.training

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.databinding.FragmentTrainingBinding
import com.cursointermedio.myapplication.ui.training.adapter.TrainingAdapter
import com.cursointermedio.myapplication.ui.training.adapter.TrainingMenuActions
import com.cursointermedio.myapplication.ui.training.dialog.TrainingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction


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
                onChangeName = { id ->
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
    }

    private fun initListener() {
        binding.ivPlus.setupTouchAction {
            createTrainingDialog()
        }
        binding.ivDownload.setupTouchAction {
        }

        observeTrainingId()
        observeTrainingHashCode()
        observeTrainingUiState()
    }

    private fun showLoading() {
        binding.shimmerLayout.startShimmer()
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.rvTraining.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
        binding.rvTraining.visibility = View.VISIBLE
    }

    private fun observeTrainingUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                trainingViewModel.uiState.collect { state ->
                    handleUiState(state)
                }
            }
        }
    }

    private fun observeTrainingId() {
        trainingViewModel.trainingId.observe(viewLifecycleOwner) { trainingId ->
            trainingId?.let { navigateToWeek(it) } ?: run {
                showToast("Error al insertar el entrenamiento")
            }
        }
    }

    private fun observeTrainingHashCode() {
        trainingViewModel.trainingHashCode.observe(viewLifecycleOwner) { hash ->
            hash ?: showToast("Error al intentar compartir el entramiento")
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun handleUiState(state: TrainingsUiState) {
        when (state) {
            is TrainingsUiState.Loading -> showLoading()
            is TrainingsUiState.Success -> {
                hideLoading()
                adapter.submitList(state.trainings)
                sizeListTraining = state.trainings.size.toString()
            }

            is TrainingsUiState.Error -> {
                hideLoading()
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToWeek(trainingId: Long) {
        findNavController().navigate(
            TrainingFragmentDirections.actionTrainingFragmentToWeekFragment(
                id = trainingId
            )
        )
    }

    private fun createTrainingDialog() {
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

    override fun onStart() {
        super.onStart()
        binding.shimmerLayout.startShimmer() // Inicia la animación al iniciar el fragmento
    }

    override fun onStop() {
        super.onStop()
        binding.shimmerLayout.stopShimmer() // Detiene la animación al detener el fragmento
    }
}
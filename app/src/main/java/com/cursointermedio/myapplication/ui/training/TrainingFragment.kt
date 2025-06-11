package com.cursointermedio.myapplication.ui.training

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentTrainingBinding
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.ui.training.adapter.TrainingAdapter
import com.cursointermedio.myapplication.ui.training.adapter.TrainingMenuActions
import com.cursointermedio.myapplication.ui.training.dialog.DownloadTrainingDialog
import com.cursointermedio.myapplication.ui.training.dialog.ShareTrainingDialog
import com.cursointermedio.myapplication.ui.training.dialog.TrainingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged


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
                },
                onShare = { id ->
                    trainingViewModel.uploadTrainingData(id)
                },
                onEliminate = { id ->
                    saveDeleteTraining(id)
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
            downloadTrainingDialog()
        }

        observeDownloadTraining()
        observeTrainingId()
        observeTrainingHashCode()
        observeTrainingUiState()
    }

    private fun observeDownloadTraining() {
        trainingViewModel.downloadState.observe(viewLifecycleOwner) { result ->
            result?.let {
                it.onSuccess {

                }
                it.onFailure { error ->
                    val message = error.message ?: "Ha ocurrido un error inesperado 😥"
                    showSnackbar(message)
                }
            }
        }
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                trainingViewModel.trainingId.collect { trainingId ->
                    trainingId?.let {
                        navigateToWeek(trainingId)
                    }
                        ?: run {
                            showSnackbar("Error al insertar el entrenamiento")
                        }
                }
            }
        }
    }

    private fun observeTrainingHashCode() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                trainingViewModel.trainingHashCode.collect{ hash ->
                    hash?.let {
                        val trainingName = hash.first
                        val code = hash.second

                        shareTrainingDialog(trainingName = trainingName, code = code)
                    }
                }
            }
        }

    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(binding.root.context, R.color.redDark))
            .setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            .show()
    }

    private fun handleUiState(state: TrainingsUiState) {
        when (state) {
            is TrainingsUiState.Loading -> showLoading()
            is TrainingsUiState.Success -> {
                hideLoading()
                adapter.submitList(state.trainings)
                sizeListTraining = state.trainings.size.toString()
                binding.rvTraining.scrollToPosition(0)
                binding.rvTraining.smoothScrollToPosition(0)
            }

            is TrainingsUiState.Error -> {
                hideLoading()
                showSnackbar(state.message)
                Log.e("ERROR", state.message)
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

    private fun downloadTrainingDialog() {
        val dialog = DownloadTrainingDialog(
            onSaveClickListener = { code ->
                trainingViewModel.downLoadTraining(code)
            }
        )
        dialog.show(parentFragmentManager, "dialog")
    }

    private fun shareTrainingDialog(trainingName: String, code: String) {
        val dialog = ShareTrainingDialog(trainingName = trainingName, code = code)
        dialog.show(parentFragmentManager, "dialog")
    }

    private fun saveDeleteTraining(training: TrainingModel) {
        val dialog = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            .setTitle(getString(R.string.training_dialog_delete_title))
            .setMessage(getString(R.string.training_dialog_delete_text, training.name))
            .setPositiveButton(getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.btn_eliminate)) { dialog, _ ->
                trainingViewModel.deleteTraining(training)
                dialog.dismiss()
            }
            .show()
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
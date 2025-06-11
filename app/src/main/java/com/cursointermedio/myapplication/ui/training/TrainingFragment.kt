package com.cursointermedio.myapplication.ui.training

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

// Fragmento principal para mostrar y gestionar la lista de entrenamientos del usuario.
// Permite crear, copiar, compartir, descargar e eliminar entrenamientos, así como navegar a las semanas de cada uno.
@AndroidEntryPoint
class TrainingFragment @Inject constructor() : Fragment() {

    private val trainingViewModel by viewModels<TrainingViewModel>()

    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrainingAdapter

    // Guarda el tamaño de la lista como String para usarlo como sugerencia en el diálogo de nuevo entrenamiento
    private lateinit var sizeListTraining: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    // Inicializa la UI y listeners principales
    private fun initUI() {
        initList()
        initListener()
    }

    // Inicializa el RecyclerView y su adaptador
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

    // Configura los listeners de los botones y observa los estados del ViewModel
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

    // Observa el resultado de la descarga de un entrenamiento
    private fun observeDownloadTraining() {
        trainingViewModel.downloadState.observe(viewLifecycleOwner) { result ->
            result?.let {
                it.onSuccess {
                    // Puedes mostrar feedback de éxito si lo deseas
                }
                it.onFailure { error ->
                    val message = error.message ?: "Ha ocurrido un error inesperado 😥"
                    showSnackbar(message)
                }
            }
        }
    }

    // Muestra el shimmer mientras se carga la lista
    private fun showLoading() {
        binding.shimmerLayout.startShimmer()
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.rvTraining.visibility = View.GONE
    }

    // Oculta el shimmer al terminar la carga
    private fun hideLoading() {
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
        binding.rvTraining.visibility = View.VISIBLE
    }

    // Observa el estado principal de la lista de entrenamientos
    private fun observeTrainingUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                trainingViewModel.uiState.collect { state ->
                    handleUiState(state)
                }
            }
        }
    }

    // Observa el nuevo ID de entrenamiento insertado y navega automáticamente si es exitoso
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

    // Observa el resultado de compartir un entrenamiento (nombre y código generado)
    private fun observeTrainingHashCode() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                trainingViewModel.trainingHashCode.collect { hash ->
                    hash?.let {
                        val trainingName = hash.first
                        val code = hash.second
                        shareTrainingDialog(trainingName = trainingName, code = code)
                    }
                }
            }
        }
    }

    // Muestra Snackbar de error o información
    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(binding.root.context, R.color.redDark))
            .setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            .show()
    }

    // Maneja los distintos estados de UI de la lista de entrenamientos
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

    // Navega al fragmento de semanas del entrenamiento seleccionado
    private fun navigateToWeek(trainingId: Long) {
        findNavController().navigate(
            TrainingFragmentDirections.actionTrainingFragmentToWeekFragment(
                id = trainingId
            )
        )
    }

    // Muestra el diálogo para crear un nuevo entrenamiento
    private fun createTrainingDialog() {
        val dialog = TrainingDialog(
            onSaveClickListener = { name ->
                trainingViewModel.insertTrainingAndWeek(name)
            }, sizeListTraining
        )
        dialog.show(parentFragmentManager, "dialog")
    }

    // Muestra el diálogo para importar un entrenamiento por código
    private fun downloadTrainingDialog() {
        val dialog = DownloadTrainingDialog(
            onSaveClickListener = { code ->
                trainingViewModel.downLoadTraining(code)
            }
        )
        dialog.show(parentFragmentManager, "dialog")
    }

    // Muestra el diálogo para compartir un entrenamiento (ver código)
    private fun shareTrainingDialog(trainingName: String, code: String) {
        val dialog = ShareTrainingDialog(trainingName = trainingName, code = code)
        dialog.show(parentFragmentManager, "dialog")
    }

    // Muestra el diálogo para confirmar la eliminación de un entrenamiento
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

    // Infla el layout del fragmento con ViewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    // Limpia el binding al destruir la vista
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Inicia o detiene la animación shimmer según el ciclo de vida
    override fun onStart() {
        super.onStart()
        binding.shimmerLayout.startShimmer()
    }

    override fun onStop() {
        super.onStop()
        binding.shimmerLayout.stopShimmer()
    }
}
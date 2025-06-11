package com.cursointermedio.myapplication.ui.routine

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentRoutineBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey
import com.cursointermedio.myapplication.ui.exercise.adapter.ExerciseAdapter
import com.cursointermedio.myapplication.ui.routine.adapter.DragExerciseAdapter
import com.cursointermedio.myapplication.ui.routine.dialog.ExerciseDescriptionDialog
import com.cursointermedio.myapplication.ui.training.CurrentFeature
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.cursointermedio.myapplication.utils.extensions.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Fragmento encargado de mostrar y gestionar una rutina con su lista de ejercicios.
// Permite ver, editar el orden, eliminar y añadir ejercicios a la rutina.
@AndroidEntryPoint
class RoutineFragment : Fragment() {

    // ViewModel para manejar la lógica de rutinas
    private val routineViewModel: RoutineViewModel by viewModels()

    // Referencia a la característica actual (puede usarse para analíticas o navegación global)
    private val currentFeature = CurrentFeature.Feature

    // ViewBinding para acceso seguro a las vistas
    private var _binding: FragmentRoutineBinding? = null
    private val binding get() = _binding!!

    // Adaptador para mostrar ejercicios (sin drag)
    private lateinit var adapter: ExerciseAdapter
    // Adaptador para drag & drop de ejercicios
    private lateinit var dragAdapter: DragExerciseAdapter
    // Helper para drag & drop
    private lateinit var itemTouchHelper: ItemTouchHelper

    // Argumentos de navegación (ID de la rutina)
    private val args: RoutineFragmentArgs by navArgs()
    private var routineId: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtiene el id de la rutina desde los argumentos de navegación
        routineId = args.id.toLong()
        // Marca la característica actual del flujo
        currentFeature.setFeature(CurrentFeature.TypeFeature.RoutineFeature)
        // Inicializa la UI y listeners
        initUI()
        initListener()
    }

    // Inicializa listeners de UI y observa cambios del ViewModel
    private fun initListener() {
        // Añadir ejercicio
        binding.ivPlusExercise.setupTouchAction {
            navigateToAddExercise()
        }

        // Volver atrás
        binding.flBack.setupTouchAction {
            findNavController().popBackStack()
        }

        // Activar modo edición (drag & drop)
        binding.btnEditRoutine.setupTouchAction {
            binding.btnEditRoutine.visibility = View.GONE
            binding.btnRoutineOk.visibility = View.VISIBLE

            val layoutManager = LinearLayoutManager(context)
            binding.rvExercise.layoutManager = layoutManager
            binding.rvExercise.adapter = dragAdapter
        }

        // Confirmar edición y guardar orden
        binding.btnRoutineOk.setupTouchAction {
            binding.btnEditRoutine.visibility = View.VISIBLE
            binding.btnRoutineOk.visibility = View.GONE

            when (val state = routineViewModel.routineWithExercise.value) {
                is RoutineUiState.Error -> {}
                RoutineUiState.Loading -> {}
                is RoutineUiState.Success -> routineViewModel.changeOrderRoutines(dragAdapter.currentList)
            }

            val layoutManager = LinearLayoutManager(context)
            binding.rvExercise.layoutManager = layoutManager
            binding.rvExercise.adapter = adapter
        }

        // Observa cambios en la rutina y actualiza UI en tiempo real
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                routineViewModel.routineWithExercise.collectLatest { state ->
                    when (state) {
                        RoutineUiState.Loading -> {
                            // Puedes mostrar shimmer/cargando aquí si quieres
                        }
                        is RoutineUiState.Success -> {
                            // Si la rutina no tiene ejercicios, muestra layout vacío
                            if (state.weeksWithRoutines.exercises.isEmpty()) {
                                activateLayoutNotExercise()
                            } else {
                                disableLayoutNotExercise()
                            }
                            // Actualiza título y listas de ejercicios (ambos adaptadores)
                            binding.tvTitle.text = state.weeksWithRoutines.routine.name
                            adapter.submitList(state.weeksWithRoutines.exercises)
                            dragAdapter.submitList(state.weeksWithRoutines.exercises)
                        }
                        is RoutineUiState.Error -> {
                            showSnackbar(binding.root, state.message, binding.root.context)
                        }
                    }
                }
            }
        }
    }

    // Oculta el layout de "sin ejercicios" y muestra la lista de ejercicios
    private fun disableLayoutNotExercise() {
        binding.llRoutineExercisesShimmer.visibility = View.GONE
        binding.rvExercise.visibility = View.VISIBLE
        binding.btnEditRoutine.visibility = View.VISIBLE
    }

    // Muestra el layout de "sin ejercicios" y oculta la lista
    private fun activateLayoutNotExercise() {
        binding.llRoutineExercisesShimmer.visibility = View.VISIBLE
        binding.rvExercise.visibility = View.GONE
        binding.btnEditRoutine.visibility = View.GONE
    }

    // Inicializa adaptadores y el layout de la lista de ejercicios
    private fun initUI() {
        // Adaptador para ver ejercicios normalmente
        adapter = ExerciseAdapter(
            onItemSelected = { exerciseId ->
                navigateToExercise(exerciseId)
            },
            menuActions = ExerciseMenuActions(
                onDescription = { exercise ->
                    createExerciseDescriptionDialog(exercise)
                },
                onEliminate = { exercise ->
                    saveDeleteTraining(exercise)
                }
            )
        )
        binding.rvExercise.layoutManager = LinearLayoutManager(context)
        binding.rvExercise.adapter = adapter

        // Adaptador para drag & drop
        dragAdapter = DragExerciseAdapter(requireContext())
        setUpItemTouchHelper()
    }

    // Muestra el diálogo de descripción de ejercicio
    private fun createExerciseDescriptionDialog(exercise: ExerciseModel) {
        val dialog = ExerciseDescriptionDialog(
            exercise = exercise,
            context = requireContext()
        )
        dialog.show(parentFragmentManager, "dialog")
    }

    // Muestra diálogo de confirmación para eliminar un ejercicio
    private fun saveDeleteTraining(exercise: ExerciseModel) {
        val dialog = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            .setTitle(getString(R.string.week_dialog_delete_title))
            .setMessage(
                getString(
                    R.string.week_dialog_delete_text,
                    exercise.getExerciseNameFromKey(requireContext())
                )
            )
            .setPositiveButton(getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.btn_eliminate)) { dialog, _ ->
                routineViewModel.deleteRoutine(exercise)
                dialog.dismiss()
            }
            .show()
    }

    // Navega al detalle de un ejercicio
    private fun navigateToExercise(exerciseId: Long) {
        findNavController().navigate(
            RoutineFragmentDirections.actionRoutineFragmentToExerciseFragment(
                routineId = routineId,
                exerciseId = exerciseId
            )
        )
    }

    // Navega al fragmento para añadir un nuevo ejercicio a la rutina
    private fun navigateToAddExercise() {
        findNavController().navigate(
            RoutineFragmentDirections.actionRoutineFragmentToAddExerciseFragment(routineId)
        )
    }

    // Configura el drag & drop de ejercicios en el RecyclerView
    private fun setUpItemTouchHelper() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                dragAdapter.moveItem(from, to)
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // No swipe action
            }
            // Efecto visual al arrastrar
            override fun onSelectedChanged(
                viewHolder: RecyclerView.ViewHolder?,
                actionState: Int
            ) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
                    viewHolder.itemView.elevation = 24f
                    viewHolder.itemView.translationZ = 24f
                    viewHolder.itemView.alpha = 0.7f
                    viewHolder.itemView.scaleX = 1.05f
                    viewHolder.itemView.scaleY = 1.05f
                }
            }
            override fun isLongPressDragEnabled(): Boolean = true
            // Restaurar vista al dejar de arrastrar
            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.elevation = 0f
                viewHolder.itemView.translationZ = 0f
                viewHolder.itemView.alpha = 1f
                viewHolder.itemView.scaleX = 1f
                viewHolder.itemView.scaleY = 1f
            }
        }
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvExercise)
    }

    // Infla la vista del fragmento usando ViewBinding
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
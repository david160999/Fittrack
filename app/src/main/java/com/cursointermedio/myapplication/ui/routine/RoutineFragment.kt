package com.cursointermedio.myapplication.ui.routine

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
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
import com.cursointermedio.myapplication.data.database.entities.ExerciseDetailsCount
import com.cursointermedio.myapplication.data.database.entities.RoutineWithExercises
import com.cursointermedio.myapplication.databinding.FragmentRoutineBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey
import com.cursointermedio.myapplication.ui.exercise.adapter.ExerciseAdapter
import com.cursointermedio.myapplication.ui.routine.adapter.DragExerciseAdapter
import com.cursointermedio.myapplication.ui.routine.dialog.ExerciseDescriptionDialog
import com.cursointermedio.myapplication.ui.training.CurrentFeature
import com.cursointermedio.myapplication.ui.week.adapter.DragRoutineAdapter
import com.cursointermedio.myapplication.ui.week.dialog.calendar.AddCalendarDialog
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.cursointermedio.myapplication.utils.extensions.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RoutineFragment : Fragment() {

    private val routineViewModel: RoutineViewModel by viewModels()


    private val currentFeature = CurrentFeature.Feature


    private var _binding: FragmentRoutineBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExerciseAdapter
    private lateinit var routine: RoutineWithExercises
    private lateinit var detailsCountList: List<ExerciseDetailsCount>

    private val args: RoutineFragmentArgs by navArgs()
    private var routineId: Long = 0

    private lateinit var dragAdapter: DragExerciseAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        routineId = args.id.toLong()
        currentFeature.setFeature(CurrentFeature.TypeFeature.RoutineFeature)
        initUI()
        initListener()
    }

    private fun initListener() {
        binding.ivPlusExercise.setupTouchAction {
            navigateToAddExercise()
        }

        binding.flBack.setupTouchAction {
            findNavController().popBackStack()
        }

        binding.btnEditRoutine.setupTouchAction {
            binding.btnEditRoutine.visibility = View.GONE
            binding.btnRoutineOk.visibility = View.VISIBLE

            val layoutManager = LinearLayoutManager(context)
            binding.rvExercise.layoutManager = layoutManager
            binding.rvExercise.adapter = dragAdapter
        }

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                routineViewModel.routineWithExercise.collectLatest { state ->
                    when (state) {
                        RoutineUiState.Loading -> {
                        }

                        is RoutineUiState.Success -> {
                            if (state.weeksWithRoutines.exercises.isEmpty()) {
                                activateLayoutNotExercise()
                            } else {
                                disableLayoutNotExercise()
                            }

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

    private fun disableLayoutNotExercise() {
        binding.llRoutineExercisesShimmer.visibility = View.GONE
        binding.rvExercise.visibility = View.VISIBLE
        binding.btnEditRoutine.visibility = View.VISIBLE
    }

    private fun activateLayoutNotExercise() {
        binding.llRoutineExercisesShimmer.visibility = View.VISIBLE
        binding.rvExercise.visibility = View.GONE
        binding.btnEditRoutine.visibility = View.GONE
    }

    private fun initUI() {

        adapter = ExerciseAdapter(
            onItemSelected = { exerciseId ->
                navigateToExercise(exerciseId)
            }, menuActions = ExerciseMenuActions(
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

        dragAdapter = DragExerciseAdapter(requireContext())
        setUpItemTouchHelper()
    }

    private fun createExerciseDescriptionDialog(exercise: ExerciseModel) {
        val dialog = ExerciseDescriptionDialog(
            exercise = exercise,
            context = requireContext()
        )
        dialog.show(parentFragmentManager, "dialog")
    }

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


    private fun navigateToExercise(exerciseId: Long) {
        findNavController().navigate(
            RoutineFragmentDirections.actionRoutineFragmentToExerciseFragment(
                routineId = routineId,
                exerciseId = exerciseId
            )
        )
    }

    private fun navigateToAddExercise() {
        findNavController().navigate(
            RoutineFragmentDirections.actionRoutineFragmentToAddExerciseFragment(routineId)
        )
    }

    private fun setUpItemTouchHelper() {
        // Setup ItemTouchHelper
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

            override fun onSelectedChanged(
                viewHolder: RecyclerView.ViewHolder?,
                actionState: Int
            ) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
                    viewHolder.itemView.elevation = 24f  // Puedes ajustar la elevación a tu gusto
                    viewHolder.itemView.translationZ = 24f
                    viewHolder.itemView.alpha = 0.7f // más bajo = más translúcido
                    viewHolder.itemView.scaleX = 1.05f
                    viewHolder.itemView.scaleY = 1.05f
                }
            }

            override fun isLongPressDragEnabled(): Boolean = true

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.elevation = 0f // Restaurar a su valor original
                viewHolder.itemView.translationZ = 0f
                viewHolder.itemView.alpha = 1f // más bajo = más translúcido
                viewHolder.itemView.scaleX = 1f
                viewHolder.itemView.scaleY = 1f
            }
        }

        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvExercise)
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
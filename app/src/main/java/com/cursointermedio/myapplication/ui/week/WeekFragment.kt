package com.cursointermedio.myapplication.ui.week

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.cursointermedio.myapplication.databinding.FragmentWeekBinding
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.ui.routine.adapter.RoutineAdapter
import com.cursointermedio.myapplication.ui.routine.adapter.RoutineMenuActions
import com.cursointermedio.myapplication.ui.routine.dialog.RoutineDialog
import com.cursointermedio.myapplication.ui.training.CurrentFeature.*
import com.cursointermedio.myapplication.ui.training.CurrentFeature.TypeFeature.*
import com.cursointermedio.myapplication.ui.week.adapter.DragRoutineAdapter
import com.cursointermedio.myapplication.ui.week.dialog.calendar.AddCalendarDialog
import com.cursointermedio.myapplication.ui.week.dialog.WeekDialog
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.cursointermedio.myapplication.utils.extensions.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class WeekFragment @Inject constructor() : Fragment() {

    private val currentFeature = Feature

    private val weekViewModel: WeekViewModel by viewModels()

    private var _binding: FragmentWeekBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RoutineAdapter
    private lateinit var dragAdapter: DragRoutineAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    private val args: WeekFragmentArgs by navArgs()
    private var trainingId: Long = 0

    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private var numWeekSpinnerSelected: Int = 0

    private var notRoutineLayout = true


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentFeature.setFeature(WeekFeature)
        trainingId = args.id
        initUI()
        initListener()

    }

    private fun initListener() {
        binding.ivPlusWeek.setupTouchAction {
            createDialog()
        }

        binding.ivWeekAddRoutine.setupTouchAction {
            createRoutineDialog()
        }
        binding.btnWeekEdit.setupTouchAction {
            setUpItemTouchHelper()
        }

        binding.btnWeekCalendar.setupTouchAction {
            addToCalendarDialog()
        }

        binding.dropMenu.setOnItemClickListener { parent, view, position, id ->
            numWeekSpinnerSelected = position
            updateRoutineAdapter()
        }

        binding.tvTitle.setupTouchAction {
            findNavController().popBackStack()
        }
        observeSpinnerList()
        observeWeekUiState()
        observeTrainingName()
    }

    private fun observeTrainingName() {
        lifecycleScope.launch {
            weekViewModel.trainingName.collect { name ->
                binding.tvTitle.text = name
            }
        }
    }

    private fun observeSpinnerList() {
        lifecycleScope.launchWhenStarted {
            weekViewModel.spinnerList.collect { list ->
                // Solo actualizar los datos del Adapter, sin recrearlo
                spinnerAdapter.clear()
                spinnerAdapter.addAll(list)
                setupWeekSpinnerLastItem()
                spinnerAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun observeWeekUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weekViewModel.weeksWithRoutines.collect { state ->
                    handleUiState(state)
                }
            }
        }
    }

    private fun showLoading() {
    }

    private fun hideLoading() {
    }

    private fun handleUiState(state: WeekUiState) {
        when (state) {
            is WeekUiState.Loading -> showLoading()
            is WeekUiState.Success -> {
                hideLoading()
                updateRoutineAdapter()

            }

            is WeekUiState.Error -> {
                hideLoading()
                showSnackbar(binding.root, state.message, binding.root.context)
            }
        }
    }

    private fun disableLayoutNotRoutines() {
        binding.flWeekRoutineShimmer.visibility = View.GONE
        binding.ivPlusWeek.visibility = View.VISIBLE
        binding.btnWeekCalendar.visibility = View.VISIBLE
        binding.btnWeekEdit.visibility = View.VISIBLE
        binding.rvRoutine.visibility = View.VISIBLE
    }

    private fun setLayoutNotRoutines() {
        binding.flWeekRoutineShimmer.visibility = View.VISIBLE
        binding.rvRoutine.visibility = View.GONE
        binding.ivPlusWeek.visibility = View.GONE
        binding.btnWeekCalendar.visibility = View.GONE
        binding.btnWeekEdit.visibility = View.GONE
    }

    private fun updateRoutineAdapter() {

        val updatedRoutine =
            weekViewModel.weeks.value.getOrNull(numWeekSpinnerSelected)?.routineList.orEmpty()

        if (updatedRoutine.isEmpty()) {
            notRoutineLayout = true
            setLayoutNotRoutines()
        } else if (notRoutineLayout) {
            disableLayoutNotRoutines()
            notRoutineLayout = false

        }

        adapter.submitList(updatedRoutine)

    }

    private fun initUI() {
        adapter = RoutineAdapter(
            onItemSelected = { routineId ->
                navigateToRoutine(routineId)
            },
            menuActions = RoutineMenuActions(
                onChangeName = { id ->
                    weekViewModel.changeNameRoutine(id)
                },
                onCopy = { id ->
                    lifecycleScope.launch {
                        weekViewModel.copyRoutine(id)
                    }
                },
                onEliminate = { id ->
                    saveDeleteTraining(id)
                }
            ),
        )
        val layoutManager = LinearLayoutManager(context)
        binding.rvRoutine.layoutManager = layoutManager
        binding.rvRoutine.adapter = adapter

        spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf<String>()
        )
        val dropMenu = binding.dropMenu
        dropMenu.setAdapter(spinnerAdapter)
    }

    private fun setUpItemTouchHelper() {
        dragAdapter =
            DragRoutineAdapter(weekViewModel.weeks.value[numWeekSpinnerSelected].routineList)

        val layoutManager = LinearLayoutManager(context)
        binding.rvRoutine.layoutManager = layoutManager
        binding.rvRoutine.adapter = dragAdapter

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
        }

        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvRoutine)
    }

//    private fun getSelectedItemFromDropMenu(): Int {
//        val weekTitles = weekViewModel.spinnerList.value
//        val selectedText = binding.dropMenu.text.toString()
//
//        return weekTitles.indexOf(selectedText)
//    }

    private fun setupWeekSpinnerLastItem() {
        val dropMenu = binding.dropMenu

        val itemCount = binding.dropMenu.adapter?.count ?: 0
        if (itemCount > 0) {
            val item = dropMenu.adapter.getItem(itemCount - 1)
            dropMenu.setText(item.toString(), false)
            numWeekSpinnerSelected = itemCount - 1
        }
    }


    private fun createDialog() {
        val weekId = weekViewModel.weeks.value.getOrNull(numWeekSpinnerSelected)?.week?.weekId

        val dialog = WeekDialog(onSaveClickListener = { option ->
            lifecycleScope.launch {
                weekViewModel.createCopyOfWeek(
                    weekId, trainingId, option
                )
            }
        }
        )

        dialog.show(parentFragmentManager, "dialog")
    }

    private fun addToCalendarDialog() {

        val dialog = AddCalendarDialog(
            onSaveClickListener = { routineList, removeDateList ->
                weekViewModel.insertDatesToRoutines(
                    routineList = routineList,
                    removeDateList = removeDateList
                )
            },
            weekViewModel.weeks.value[numWeekSpinnerSelected].routineList
        )

        dialog.show(parentFragmentManager, "dialog")
    }

    private fun createRoutineDialog() {
        val weekId = weekViewModel.weeks.value.getOrNull(numWeekSpinnerSelected)?.week?.weekId

        if (weekId != null) {
            val numRoutines = weekViewModel.weeks.value[numWeekSpinnerSelected].routineList.size

            val dialog = RoutineDialog(onSaveClickListener = { name ->
                lifecycleScope.launch {
                    val routine = RoutineModel(null, weekId, name, null)
                    weekViewModel.insertRoutine(routine)
                }
            }, numRoutines)
            dialog.show(parentFragmentManager, "dialog")
        }
    }

    private fun saveDeleteTraining(routine: RoutineModel) {
        val dialog = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            .setTitle(getString(R.string.week_dialog_delete_title))
            .setMessage(getString(R.string.week_dialog_delete_text, routine.name))
            .setPositiveButton(getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.btn_eliminate)) { dialog, _ ->
                weekViewModel.deleteRoutine(routine)
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToRoutine(routineId: Long) {
        findNavController().navigate(
            WeekFragmentDirections.actionWeekFragmentToRoutineFragment(
                routineId.toInt()
            )
        )
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeekBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
//        weekViewModel.loadItems()
    }
}



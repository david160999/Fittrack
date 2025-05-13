package com.cursointermedio.myapplication.ui.week

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.databinding.FragmentWeekBinding
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.ui.routine.adapter.RoutineAdapter
import com.cursointermedio.myapplication.ui.routine.dialog.RoutineDialog
import com.cursointermedio.myapplication.ui.training.CurrentFeature.*
import com.cursointermedio.myapplication.ui.training.CurrentFeature.TypeFeature.*
import com.cursointermedio.myapplication.ui.week.dialog.WeekDialog
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.cursointermedio.myapplication.utils.extensions.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class WeekFragment : Fragment() {

    private val currentFeature = Feature

    private val weekViewModel: WeekViewModel by viewModels()

    private var _binding: FragmentWeekBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RoutineAdapter
    private lateinit var sizeListWeek: String

    private lateinit var adapterWeeks: ArrayAdapter<WeekWithRoutines>

    private val args: WeekFragmentArgs by navArgs()
    private var trainingId: Long = 0

    private var spinnerInitialized = false
    private lateinit var spinnerAdapter: ArrayAdapter<String>


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

        binding.ivPlus.setupTouchAction {
            createRoutineDialog()
        }

        binding.dropMenu.setOnItemClickListener { parent, view, position, id ->
            val selectedWeek = weekViewModel.weeks.value[position]
            adapter.submitList(selectedWeek.routineList)
        }

        observeWeekUiState()
        observeSpinnerList()
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

                val selectedItem = getSelectedItemFromDropMenu()
                val updatedRoutine = state.weeksWithRoutines.getOrNull(selectedItem)?.routineList.orEmpty()

                adapter.submitList(updatedRoutine)
                binding.rvRoutine.scrollToPosition(0)
                binding.rvRoutine.smoothScrollToPosition(0)
            }

            is WeekUiState.Error -> {
                hideLoading()
                showSnackbar(binding.root, state.message, binding.root.context)
            }
        }
    }

    private fun initUI() {
        adapter = RoutineAdapter(
            onItemSelected = { routineId ->
                navigateToRoutine(routineId)
            }
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

    private fun getSelectedItemFromDropMenu(): Int {
        val weekTitles = weekViewModel.spinnerList.value
        val selectedText = binding.dropMenu.text.toString()

        return weekTitles.indexOf(selectedText)
    }

    private fun setupWeekSpinnerLastItem() {
        val dropMenu = binding.dropMenu

        val itemCount = binding.dropMenu.adapter?.count ?: 0
        if (itemCount > 0) {
            val item = dropMenu.adapter.getItem(itemCount - 1)
            dropMenu.setText(item.toString(), false)
        }
    }


    private fun createDialog() {
        val weekId = getSelectedWeekId()

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

    private fun createRoutineDialog() {
        val weekId = getSelectedWeekId()

        if (weekId != null) {
            val numRoutines = weekViewModel.weeks.value[weekId.toInt()].routineList.size

            val dialog = RoutineDialog(onSaveClickListener = { name ->
                lifecycleScope.launch {
                    val routine = RoutineModel(null, weekId, name, null)
                    weekViewModel.insertRoutine(routine)
                }
            }, numRoutines)
            dialog.show(parentFragmentManager, "dialog")
        }
    }

    private fun getSelectedWeekId(): Long? {
        val selected = getSelectedItemFromDropMenu()
        return weekViewModel.weeks.value.getOrNull(selected)?.week?.weekId
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

}



package com.cursointermedio.myapplication.ui.addExercise

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView   // ✅ BIEN, esta es la que corresponde al XML
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentAddexerciseBinding
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey
import com.cursointermedio.myapplication.ui.addExercise.adapterCategory.CategoryAdapter
import com.cursointermedio.myapplication.ui.addExercise.adapterExercise.AddExerciseAdapter
import com.cursointermedio.myapplication.ui.addExercise.adapterExercise.SelectedExerciseAdapter
import com.cursointermedio.myapplication.ui.addExercise.dialog.AddExerciseDialog
import com.cursointermedio.myapplication.ui.addExercise.dialog.RemoveExerciseDialog
import com.cursointermedio.myapplication.ui.routine.RoutineFragmentArgs
import com.cursointermedio.myapplication.ui.week.WeekFragmentDirections
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.cursointermedio.myapplication.utils.extensions.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddExerciseFragment : Fragment() {
    private val addExerciseViewModel: AddExerciseViewModel by viewModels()

    private var _binding: FragmentAddexerciseBinding? = null
    private val binding get() = _binding!!

    //    private lateinit var adapterExercise: AddExerciseAdapter
    private lateinit var adapterExercise: AddExerciseAdapter
    private lateinit var adapterCategory: CategoryAdapter
    private lateinit var adapterSelected: SelectedExerciseAdapter

    private var selectedExercises = MutableStateFlow<List<ExerciseModel>>(emptyList())
    private var originalList: List<ExerciseModel> = emptyList()  // Guardamos la lista original

    private var categoryList: List<CategoryInfo> = emptyList()

    private val args: AddExerciseFragmentArgs by navArgs()
    private var routineId: Long = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        routineId = args.routineId
        initUI()
        initListener()
    }

    private fun initListener() {
        binding.ivBack.setupTouchAction {
            findNavController().popBackStack()
        }

        binding.ivPlusExercise.setupTouchAction {
            createDialogAdd()
        }

        binding.tvNext.setupTouchAction {
            lifecycleScope.launch {
                addExerciseViewModel.insertExerciseToRoutine(routineId, selectedExercises.value)
                findNavController().popBackStack()
            }
        }

        binding.svExercise.setOnClickListener {
            binding.svExercise.isIconified = false
            binding.svExercise.requestFocus()
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.svExercise.findFocus(), InputMethodManager.SHOW_IMPLICIT)
        }

        binding.svExercise.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredText = newText.orEmpty()
                val filteredList = originalList.filter { exercise ->
                    val exerciseName = exercise.getExerciseNameFromKey(requireContext())
                    exerciseName.contains(filteredText, ignoreCase = true)
                }
                adapterExercise.submitList(filteredList)
                return true
            }
        })
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    addExerciseViewModel.exerciseList.collectLatest { state ->
                        when (state) {
                            is AddExerciseUiState.Error -> showSnackbar(
                                binding.root,
                                state.message,
                                requireContext()
                            )

                            AddExerciseUiState.Loading -> {}
                            is AddExerciseUiState.Success -> {
                                originalList = state.exercises
                                adapterExercise.submitList(state.exercises)
                            }
                        }
                    }
                }
                launch {
                    addExerciseViewModel.categoryList.collectLatest { state ->
                        when (state) {
                            is CategoryUiState.Error -> showSnackbar(
                                binding.root,
                                state.message,
                                requireContext()
                            )

                            CategoryUiState.Loading -> {}
                            is CategoryUiState.Success -> {
                                categoryList = state.categoryList
                                adapterCategory.submitList(state.categoryList)
                            }
                        }
                    }
                }
                launch {
                    selectedExercises.collectLatest { exerciseList ->
                        if (exerciseList.isNotEmpty()) {
                            binding.tvNext.visibility = View.VISIBLE
                        } else {
                            binding.tvNext.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun initUI() {
        setUpSelectedAdapter()
        setUpExerciseAdapter()
        setUpCategoryAdapter()
    }

    private fun setUpSelectedAdapter() {
        adapterSelected = SelectedExerciseAdapter(
            onItemSelected = { exercise ->
                createDialogRemove(exercise)
            }
        )
        binding.lvAddExercises.layoutManager = LinearLayoutManager(context)
        binding.lvAddExercises.adapter = adapterSelected

        val dividerItemDecoration = DividerItemDecoration(
            binding.rvExercise.context,
            LinearLayoutManager.VERTICAL // Asegúrate de que sea un layout vertical
        )
        binding.lvAddExercises.addItemDecoration(dividerItemDecoration)
    }

    private fun setUpExerciseAdapter() {
        adapterExercise = AddExerciseAdapter(
            onItemSelected = { addExercise ->
                addToListAddExercise(addExercise)
            }
        )

        binding.rvExercise.layoutManager = LinearLayoutManager(context)
        binding.rvExercise.adapter = adapterExercise

        val dividerItemDecoration =
            DividerItemDecoration(binding.rvExercise.context, LinearLayoutManager.VERTICAL)
        binding.rvExercise.addItemDecoration(dividerItemDecoration)
    }

    private fun setUpCategoryAdapter() {
        adapterCategory =
            CategoryAdapter { categoryId, isSelected ->
                selectCategory(
                    categoryId,
                    isSelected
                )
            }
        binding.rvCategory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategory.adapter = adapterCategory

    }

    private fun addToListAddExercise(exercise: ExerciseModel) {
        if (!selectedExercises.value.contains(exercise)) {
            val newList = selectedExercises.value.toMutableList()
            newList.add(exercise)
            selectedExercises.value = newList
            adapterSelected.submitList(newList)
        }
    }

    private fun removeToListAddExercise(exercise: ExerciseModel) {
        val newList = selectedExercises.value.toMutableList()  // copia mutable nueva
        newList.remove(exercise)                          // elimina el ejercicio
        selectedExercises.value = newList                       // asigna la lista nueva
        adapterSelected.submitList(newList)               // pasa la nueva lista al adapter
    }

    private fun selectCategory(categoryId: Long, isSelected: Boolean) {
        if (isSelected) {
            addExerciseViewModel.getAllExercise()
        } else {
            addExerciseViewModel.getExercisesFromCategory(categoryId)
        }
    }

    private fun createDialogAdd() {
        val dialog = AddExerciseDialog(onSaveClickListener = { exercise ->
            lifecycleScope.launch {
                addExerciseViewModel.insertExercise(exercise)
            }
        }, this.categoryList)

        dialog.show(parentFragmentManager, "dialog")

    }

    private fun createDialogRemove(exercise: ExerciseModel) {

        val dialog = RemoveExerciseDialog(onSaveClickListener = {
            lifecycleScope.launch {
                removeToListAddExercise(exercise)
            }
        }, exercise = exercise)

        dialog.show(parentFragmentManager, "dialog")
    }

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddexerciseBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

}
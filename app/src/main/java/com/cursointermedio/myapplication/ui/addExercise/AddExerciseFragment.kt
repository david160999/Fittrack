package com.cursointermedio.myapplication.ui.addExercise

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.FragmentAddexerciseBinding
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.ui.addExercise.adapterCategory.CategoryAdapter
import com.cursointermedio.myapplication.ui.addExercise.adapterExercise.AddExerciseAdapter
import com.cursointermedio.myapplication.ui.addExercise.adapterExercise.SelectedExerciseAdapter
import com.cursointermedio.myapplication.ui.addExercise.dialog.AddExerciseDialog
import com.cursointermedio.myapplication.ui.addExercise.dialog.RemoveExerciseDialog
import com.cursointermedio.myapplication.ui.routine.RoutineFragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
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

    private lateinit var exercises: Flow<List<ExerciseModel>>
    private var selectedExercises = mutableListOf<ExerciseModel>()

    private lateinit var categories: List<CategoryInfo>
    private var nameCategory = ""

    private val args: AddExerciseFragmentArgs by navArgs()
    private var routineId: Long = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        routineId = args.routineId
        initUI()
        initListener()
    }

    private fun initListener() {
        binding.ivBack.setAlphaTouchListener {
            findNavController().popBackStack()
        }

        binding.ivPlusExercise.setAlphaTouchListener {
            createDialogAdd()
        }

        binding.tvNext.setAlphaTouchListener {
            lifecycleScope.launch {
                addExerciseViewModel.insertExerciseToRoutine(routineId, selectedExercises)

            }
        }
    }


    private fun initUI() {
        exercises = addExerciseViewModel.getAllExercise()

        setUpSelectedAdapter()

        lifecycleScope.launch {
            setUpCategoryAdapter()

            exercises.collect { exercise ->
                // Solo hacemos esto si hay datos
                if (exercise.isNotEmpty()) {
                    if (!::adapterExercise.isInitialized) {
                        setUpExerciseAdapter(exercise)
                    } else {
                        adapterExercise.updateList(exercise)
                    }
                }
            }

        }

    }


    private fun setUpSelectedAdapter() {
        adapterSelected = SelectedExerciseAdapter(
            onItemSelected = { exercise ->
                createDialogRemove(exercise)
            },
            selectedExercises
        )
        binding.lvAddExercises.layoutManager = LinearLayoutManager(context)
        binding.lvAddExercises.adapter = adapterSelected

        val dividerItemDecoration = DividerItemDecoration(
            binding.rvExercise.context,
            LinearLayoutManager.VERTICAL // Asegúrate de que sea un layout vertical
        )
        binding.lvAddExercises.addItemDecoration(dividerItemDecoration)

        adapterSelected.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                updateNextVisibility()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                updateNextVisibility()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                updateNextVisibility()
            }
        })
    }

    private fun updateNextVisibility() {
        binding.tvNext.isVisible = adapterSelected.itemCount != 0
    }

    private fun setUpExerciseAdapter(exercise: List<ExerciseModel>) {
        adapterExercise = AddExerciseAdapter(
            onItemSelected = { addExercise ->
                addToListAddExercise(addExercise)
            }, exercises = exercise
        )

        binding.rvExercise.layoutManager = LinearLayoutManager(context)
        binding.rvExercise.adapter = adapterExercise

        val dividerItemDecoration = DividerItemDecoration(
            binding.rvExercise.context,
            LinearLayoutManager.VERTICAL // Asegúrate de que sea un layout vertical
        )
        binding.rvExercise.addItemDecoration(dividerItemDecoration)
    }

    private suspend fun setUpCategoryAdapter() {
        this.categories = addExerciseViewModel.getCategories()

        if (this.categories.isNotEmpty()) {
            adapterCategory =
                CategoryAdapter({ categoryId, isSelected ->
                    selectCategory(
                        categoryId,
                        isSelected
                    )
                }, this.categories)
            binding.rvCategory.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvCategory.adapter = adapterCategory
        }
    }

    private fun addToListAddExercise(exercise: ExerciseModel) {
        selectedExercises.add(exercise) // compatible con todas las APIs
        adapterSelected.updateList(selectedExercises)
    }

    private fun removeToListAddExercise(exercise: ExerciseModel) {
        selectedExercises.remove(exercise) // compatible con todas las APIs
        adapterSelected.updateList(selectedExercises)
    }

    private suspend fun selectCategory(categoryId: Long, isSelected: Boolean) {
        if (isSelected) {
            val exerciseList = addExerciseViewModel.getAllExercise()
            exerciseList.collect {
                adapterExercise.updateList(it)
            }
        } else {
            val filteredList = addExerciseViewModel.getExercisesFromCategory(categoryId)
            adapterExercise.updateList(filteredList)
        }


    }

    private fun createDialogAdd() {
            val dialog = AddExerciseDialog(onSaveClickListener = { exercise ->
                lifecycleScope.launch {
                    addExerciseViewModel.insertExercise(exercise)
                }
            }, this.categories)

            dialog.show(parentFragmentManager, "dialog")

    }
    private fun createDialogRemove(exercise: ExerciseModel) {

        val dialog = RemoveExerciseDialog(onSaveClickListener = {
            lifecycleScope.launch {
                removeToListAddExercise(exercise)
            }
        })

        dialog.show(parentFragmentManager, "dialog")
    }

    private fun createDialogNext() {


    }


    @SuppressLint("ClickableViewAccessibility")
    private fun View.setAlphaTouchListener(onClick: (() -> Unit)? = null) {
        this.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.alpha = 0.2f
                MotionEvent.ACTION_UP -> {
                    v.alpha = 1f
                    val x = event.x
                    val y = event.y
                    if (x in 0f..v.width.toFloat() && y in 0f..v.height.toFloat()) {
                        onClick?.invoke()
                    }
                }

                MotionEvent.ACTION_CANCEL -> v.alpha = 1f
            }
            true
        }
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
package com.cursointermedio.myapplication.ui.addExercise

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.databinding.FragmentAddexerciseBinding
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.ui.addExercise.adapterCategory.CategoryAdapter
import com.cursointermedio.myapplication.ui.addExercise.adapterExercise.AddExerciseAdapter
import com.cursointermedio.myapplication.ui.addExercise.adapterExercise.SelectedExerciseAdapter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initListener()
    }

    private fun initListener() {
        binding.ivBack.setAlphaTouchListener {
            findNavController().popBackStack()
        }

        binding.ivPlusExercise.setAlphaTouchListener {

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
                createDialog(exercise)
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
        val categories = addExerciseViewModel.getCategories()

        if (categories.isNotEmpty()) {
            adapterCategory =
                CategoryAdapter({ categoryId, isSelected ->
                    selectCategory(
                        categoryId,
                        isSelected
                    )
                }, categories)
            binding.rvCategory.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvCategory.adapter = adapterCategory
        }
    }

    private fun addToListAddExercise(exercise: ExerciseModel) {
        selectedExercises.add(exercise) // compatible con todas las APIs
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

    private fun createDialog(exercise: ExerciseModel) {

//        val selected = getSelectedItemFromDropMenu()
//        val weekId = listWeekWithRoutines.value.getOrNull(selected)?.week?.weekId
//
//
//        val dialog = WeekDialog(onSaveClickListener = { option ->
//            lifecycleScope.launch {
//                when (option) {
//                    "CopyWeek", "CopyWeekWithObj", "CopyWeekWithAll" -> weekViewModel.createCopyOfWeek(
//                        weekId, trainingId, option,
//                    )
//
//                }
//            }
//        })
//
//        dialog.show(parentFragmentManager, "dialog")
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
            false
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
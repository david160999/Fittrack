package com.cursointermedio.myapplication.ui.exercise.childFragments.objectiveFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentObjectiveBinding
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.ui.exercise.DetailUiState
import com.cursointermedio.myapplication.ui.exercise.ExerciseViewModel
import com.cursointermedio.myapplication.ui.exercise.adapter.detailsAdapter.ObjDetailAdapter
import com.cursointermedio.myapplication.ui.exercise.adapter.detailsAdapter.RealDetailAdapter
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ObjectiveFragment : Fragment() {

    private val exerciseViewModel: ExerciseViewModel by viewModels({ requireParentFragment() })

    private var _binding: FragmentObjectiveBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ObjDetailAdapter

    private var detailResponse = mutableListOf<DetailModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initListeners()

    }

    private fun initUI() {
        adapter = ObjDetailAdapter(
            onItemChanged = { detail ->
                exerciseViewModel.updateList(detail)
            },
            onItemChangedFragment = { fragment -> exerciseViewModel.changeFragmentAdapter(fragment) })

        binding.rvDetail.layoutManager = LinearLayoutManager(context)
        binding.rvDetail.adapter = adapter
        binding.rvDetail.isNestedScrollingEnabled = false

    }

    private fun initListeners() {
        binding.cvPlus.setupTouchAction {
            exerciseViewModel.insertDetailToRoutineExercise()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    exerciseViewModel.detailList.collectLatest { state ->
                        when (state) {
                            is DetailUiState.Error -> {}
                            DetailUiState.Loading -> {}
                            is DetailUiState.Success -> {
                                adapter.submitList(state.detailList)
                            }
                        }
                    }
                }
                launch {
                    exerciseViewModel.detailResponseList.collectLatest { details ->
                        val tonelajeTotal = details.sumOf {
                            (it.realWeight ?: 0) * (it.realReps ?: 0)
                        }
                        binding.tvTonelaje.text = tonelajeTotal.toString()
                    }
                }
                launch {
                    exerciseViewModel.notes.collectLatest { notes ->
                        notes?.let {
                            binding.etNotes.setText(notes)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentObjectiveBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onPause() {
        super.onPause()

        exerciseViewModel.updateDetailToRoutineExercise(exerciseViewModel.detailResponseList.value)

        val notes = binding.etNotes.text.toString()
        if (notes.isNotEmpty()) {
            exerciseViewModel.updateNotesFromCrossRef(notes)
        }
    }

}
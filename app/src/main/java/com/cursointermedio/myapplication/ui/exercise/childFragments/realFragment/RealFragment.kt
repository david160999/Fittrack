package com.cursointermedio.myapplication.ui.exercise.childFragments.realFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentExerciseBinding
import com.cursointermedio.myapplication.databinding.FragmentRealBinding
import com.cursointermedio.myapplication.databinding.ItemDetailsBinding
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.ui.exercise.DetailUiState
import com.cursointermedio.myapplication.ui.exercise.ExerciseFragmentArgs
import com.cursointermedio.myapplication.ui.exercise.ExerciseViewModel
import com.cursointermedio.myapplication.ui.exercise.adapter.ExerciseAdapter
import com.cursointermedio.myapplication.ui.exercise.adapter.detailsAdapter.RealDetailAdapter
import com.cursointermedio.myapplication.ui.training.CurrentFeature
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RealFragment : Fragment() {

    private val exerciseViewModel: ExerciseViewModel by viewModels({ requireParentFragment() })

    private var _binding: FragmentRealBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RealDetailAdapter

    private var detailResponse = mutableListOf<DetailModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initListeners()

    }

    private fun initUI() {
        adapter = RealDetailAdapter(
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
                    exerciseViewModel.exerciseStatistics.collectLatest { statistics ->
                        binding.tvTonelaje.text = statistics.first.toString()
                        binding.tvResultErm.text = statistics.second.toString()
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
        _binding = FragmentRealBinding.inflate(
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
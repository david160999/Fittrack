package com.cursointermedio.myapplication.ui.week

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.data.database.entities.WeekWithRoutines
import com.cursointermedio.myapplication.databinding.FragmentWeekBinding
import com.cursointermedio.myapplication.ui.training.CurrentFeature.*
import com.cursointermedio.myapplication.ui.training.CurrentFeature.TypeFeature.*
import com.cursointermedio.myapplication.ui.week.adapter.WeekAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WeekFragment : Fragment() {

    private val currentFeature = Feature

    private val weekViewModel by viewModels<WeekViewModel>()

    private var _binding: FragmentWeekBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: WeekAdapter
    private lateinit var listWeekWithRoutines: Flow<List<WeekWithRoutines>>

    private lateinit var adapterWeeks : ArrayAdapter<WeekWithRoutines>

    private val args: WeekFragmentArgs by navArgs()
    private var trainingId: Int = 0
    private var weekId: Int = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentFeature.setFeature(WeekFeature)
        trainingId = args.id
        initUI()
    }

    private fun initUI() {
        listWeekWithRoutines = weekViewModel.getWeeksWithRoutines(trainingId)

        adapter = WeekAdapter(
            onItemSelected = { routineId ->
                navigateToRoutine(routineId)
            },
            weekId = weekId
        )

        binding.rvRoutine.layoutManager = LinearLayoutManager(context)
        binding.rvRoutine.adapter = adapter

        lifecycleScope.launch {
            listWeekWithRoutines.collect {
                adapter.updateList(it)
            }
        }

    }



    private fun navigateToRoutine(routineId: Int) {
        findNavController().navigate(
            WeekFragmentDirections.actionWeekFragmentToRoutineFragment(
                routineId
            )
        )
    }

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeekBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

}
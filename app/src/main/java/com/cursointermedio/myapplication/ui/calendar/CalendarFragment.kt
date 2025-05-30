package com.cursointermedio.myapplication.ui.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentCalendarBinding
import com.cursointermedio.myapplication.databinding.FragmentTrainingBinding
import com.cursointermedio.myapplication.ui.training.TrainingFragment
import com.cursointermedio.myapplication.ui.training.TrainingViewModel
import com.cursointermedio.myapplication.ui.training.adapter.TrainingAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private val calendarViewModel by viewModels<CalendarViewModel>()

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }
}
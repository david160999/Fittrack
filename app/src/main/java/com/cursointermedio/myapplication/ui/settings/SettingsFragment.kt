package com.cursointermedio.myapplication.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentSettingsBinding
import com.cursointermedio.myapplication.databinding.FragmentWeekBinding
import com.cursointermedio.myapplication.ui.week.WeekViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment @Inject constructor() : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private fun initUi() {

    }

    private fun initListeners() {

    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
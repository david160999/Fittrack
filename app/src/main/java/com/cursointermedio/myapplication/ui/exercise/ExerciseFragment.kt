package com.cursointermedio.myapplication.ui.exercise

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cursointermedio.myapplication.databinding.FragmentExerciseBinding
import com.cursointermedio.myapplication.ui.exercise.childFragments.ExercisePageAdapter
import com.cursointermedio.myapplication.ui.training.CurrentFeature
import com.google.android.material.tabs.TabLayoutMediator


class ExerciseFragment : Fragment() {

    private val currentFeature = CurrentFeature.Feature

    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initListener()
        currentFeature.setFeature(CurrentFeature.TypeFeature.ExerciseFeature)
    }

    private fun initListener() {
    }


    private fun initUI() {
        val tabLayout = binding.tabLayout
        val viewPager2 = binding.pager
        val pagerAdapter = ExercisePageAdapter(childFragmentManager, lifecycle)
        viewPager2.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "OBJETIVO"
                }

                1 -> {
                    tab.text = "REAL"
                }
            }
        }.attach()
    }


    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
}
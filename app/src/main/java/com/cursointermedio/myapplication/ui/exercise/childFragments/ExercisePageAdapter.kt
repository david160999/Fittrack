package com.cursointermedio.myapplication.ui.exercise.childFragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cursointermedio.myapplication.ui.exercise.childFragments.objectiveFragment.ObjectiveFragment
import com.cursointermedio.myapplication.ui.exercise.childFragments.realFragment.RealFragment

class ExercisePageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> ObjectiveFragment()
            1 -> RealFragment()
            else -> {
                throw AssertionError()
            }
        }
    }

}
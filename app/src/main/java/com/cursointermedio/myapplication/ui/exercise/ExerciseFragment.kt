package com.cursointermedio.myapplication.ui.exercise

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentExerciseBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey
import com.cursointermedio.myapplication.ui.exercise.childFragments.ExercisePageAdapter
import com.cursointermedio.myapplication.ui.routine.RoutineFragmentArgs
import com.cursointermedio.myapplication.ui.routine.dialog.ExerciseDescriptionDialog
import com.cursointermedio.myapplication.ui.training.CurrentFeature
import com.cursointermedio.myapplication.ui.training.adapter.TrainingMenuAdapter
import com.cursointermedio.myapplication.utils.extensions.isItemBelowThreshold
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExerciseFragment : Fragment() {

    private val exerciseViewModel: ExerciseViewModel by viewModels()

    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!

    private val args: ExerciseFragmentArgs by navArgs()

    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initListener()
    }

    private fun initListener() {
        binding.ivPoints.setupTouchAction {
            createDetailOptionsMenu()
        }

        binding.linearBack.setupTouchAction {
            findNavController().popBackStack()
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        exerciseViewModel.changeFragmentAdapter(0)
                    }
                    1 -> {
                        exerciseViewModel.changeFragmentAdapter(1)
                    }
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    exerciseViewModel.exercise.collectLatest { exercise ->
                        exercise?.let {
                            binding.tvTitle.text = exercise.getExerciseNameFromKey(requireContext())
                        }
                    }
                }
                launch {
                    exerciseViewModel.adapterFragment.collectLatest {
                        viewPager.setCurrentItem(it, true)
                    }
                }
            }
        }
    }

//    private fun setRealLayout() {
//        binding.lyObjDetails.visibility = View.GONE
//        binding.lyRealDetail.visibility = View.VISIBLE
//    }
//
//    private fun setObjectiveLayout() {
//        binding.lyRealDetail.visibility = View.GONE
//        binding.lyObjDetails.visibility = View.VISIBLE
//    }

    private fun createDetailOptionsMenu() {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu_training, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val recyclerView = popupView.findViewById<RecyclerView>((R.id.rvTrainingMenu))
        recyclerView.layoutManager = LinearLayoutManager(context)

        val items = listOf(
            ContextCompat.getString(requireContext(), R.string.detail_menuOption1),
            ContextCompat.getString(requireContext(), R.string.detail_menuOption2),
        )

        val adapter = TrainingMenuAdapter(items) { position ->
            when (position) {
                0 -> createExerciseDescriptionDialog(exerciseViewModel.exercise.value)
                1 -> exerciseViewModel.deleteLastDetail()
            }
            popupWindow.dismiss()
        }
        recyclerView.adapter = adapter

        popupWindow.apply {
            isFocusable = true
            isOutsideTouchable = true
            elevation = 12f
            isClippingEnabled = true
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            animationStyle = R.style.MenuTRainingPopupFadeAnimation
        }

        popupWindow.showAsDropDown(binding.ivPoints, -420, 50)

    }

    private fun initUI() {
        val tabLayout = binding.tabLayout
        viewPager = binding.pager
        val pagerAdapter = ExercisePageAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = pagerAdapter
        viewPager.setCurrentItem(1, false)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
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

    private fun createExerciseDescriptionDialog(exercise: ExerciseModel?) {
        if (exercise != null) {
            val dialog = ExerciseDescriptionDialog(
                exercise = exercise,
                context = requireContext()
            )
            dialog.show(parentFragmentManager, "dialog")
        }
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
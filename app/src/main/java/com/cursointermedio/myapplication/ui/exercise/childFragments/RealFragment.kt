package com.cursointermedio.myapplication.ui.exercise.childFragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentExerciseBinding
import com.cursointermedio.myapplication.databinding.FragmentRealBinding
import com.cursointermedio.myapplication.databinding.ItemDetailsBinding
import com.cursointermedio.myapplication.ui.exercise.adapter.ExerciseAdapter
import com.cursointermedio.myapplication.ui.training.CurrentFeature

class RealFragment : Fragment() {


    private var _binding: FragmentRealBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExerciseAdapter

    private var listDetails = listOf("qqq", "baa", "qq", "a", "a")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        adapter = ExerciseAdapter {}
        binding.rvDetail.layoutManager = LinearLayoutManager(context)
        binding.rvDetail.adapter = adapter
        adapter.updateList(listDetails)
        binding.cvPlus.setOnTouchListener(binding)
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


    @SuppressLint("ClickableViewAccessibility", "PrivateResource")
    private fun CardView.setOnTouchListener(binding: FragmentRealBinding) {
        binding.cvPlus.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvPlus.alpha = 0.2F
                    binding.ivCardPlus.alpha = 0.4F
                }

                MotionEvent.ACTION_MOVE -> {}
                MotionEvent.ACTION_UP -> {
                    binding.cvPlus.alpha = 1F
                    binding.ivCardPlus.alpha = 1F
                }

                MotionEvent.ACTION_CANCEL -> {
                    binding.cvPlus.alpha = 1F
                    binding.ivCardPlus.alpha = 1F
                }
            }
            true
        }


    }


}
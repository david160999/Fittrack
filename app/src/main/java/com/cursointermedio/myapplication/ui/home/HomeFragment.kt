package com.cursointermedio.myapplication.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.commit
import androidx.fragment.app.findFragment
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentHomeBinding
import com.cursointermedio.myapplication.databinding.FragmentSettingsBinding
import com.cursointermedio.myapplication.ui.settings.SettingsFragment
import com.cursointermedio.myapplication.ui.training.TrainingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.zip.Inflater


class HomeFragment : Fragment() {

    private val homeViewModel by viewModels<HomeViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initUI() {

        binding.lyToSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)

        }

        binding.cvTraining.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_navigation)

        }


    }
//    override  fun onResume(){
//        super.onResume()
//        Log.i("aaa", "RESUMNE")
//    }
//    override  fun onPause(){
//        super.onPause()
//        Log.i("aaa", "PAUSE")
//    }
//    override  fun onStart(){
//        super.onStart()
//        Log.i("aaa", "START")
//    }
//
//    override  fun onDestroy(){
//        super.onDestroy()
//        Log.i("aaa", "DESTROY")
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        initUI()
        return binding.root
    }

}

private fun NavController.removeOnDestinationChangedListener() {

}





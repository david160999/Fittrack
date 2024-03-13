package com.cursointermedio.myapplication.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.cursointermedio.myapplication.R

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        val btnNavigate = root.findViewById<LinearLayout>(R.id.lyToHome)

        btnNavigate.setOnClickListener{
            findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
        }
        return root
    }

}
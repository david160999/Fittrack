package com.cursointermedio.myapplication.ui.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentSettingsBinding
import com.cursointermedio.myapplication.domain.model.UserSettings
import com.cursointermedio.myapplication.ui.home.MainActivity
import com.cursointermedio.myapplication.ui.routine.RoutineUiState
import com.cursointermedio.myapplication.utils.extensions.UserDataUiSate
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.cursointermedio.myapplication.utils.extensions.showSnackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment @Inject constructor() : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedLanguage: Language

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initListeners()

    }

    private fun initUi() {
        setUpMenuLanguage()

    }

    private fun initListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    settingsViewModel.userSettingsData.collectLatest { state ->
                        when (state) {
                            is SettingsUiState.Error -> {
                                showSnackbar(binding.root.rootView, state.message, requireContext())
                            }

                            SettingsUiState.Loading -> {

                            }

                            is SettingsUiState.Success -> {

                                val userSettings = state.userSettings

                                binding.tvUserName.text = getString(R.string.home_user_name, userSettings.username)
                                binding.tvUserEmail.text = userSettings.email

                                binding.btnDarkMode.isChecked = userSettings.isDarkMode
                                binding.btnLanguage.text = state.userSettings.language.toString()
                                selectedLanguage = state.userSettings.language

                                if (userSettings.isWeightKgMode) {
                                    binding.modeToggleGroupWeight.check(R.id.btnWeightKg)
                                } else {
                                    binding.modeToggleGroupWeight.check(R.id.btnWeightLbs)
                                }

                                if (userSettings.isExerciseKgMode) {
                                    binding.modeToggleGroupExercise.check(R.id.btnExerciseKg)
                                } else {
                                    binding.modeToggleGroupExercise.check(R.id.btnExerciseLbs)
                                }

                            }
                        }

                    }

                }
                launch {
                    settingsViewModel.userData.collectLatest { state ->
                        when (state) {
                            is UserDataUiSate.Error -> {
                                showSnackbar(
                                    binding.root.rootView,
                                    state.message,
                                    requireContext()
                                )
                            }

                            UserDataUiSate.Loading -> {}
                            is UserDataUiSate.Success -> {
                                val user = state.userData

                                Glide.with(requireContext())
                                    .load(user.photoUrl)
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_circle) // Imagen temporal mientras carga
                                    .error(R.drawable.ic_circle)       // Imagen por si falla
                                    .into(binding.ivUserProfile)  // tu ImageView

                            }
                        }
                    }
                }
            }
        }

        binding.btnDarkMode.setOnClickListener {
            val isChecked = binding.btnDarkMode.isChecked
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            settingsViewModel.onDarkModeToggled(isChecked)

        }

        binding.btnCloseSession.setupTouchAction {
            createDialogCloseSession()
        }

        binding.lyGoHome.setupTouchAction {
            findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
        }
    }

    private fun setUpMenuLanguage() {
        val listPopupWindowButton = binding.btnLanguage
        val listPopupWindow = ListPopupWindow(requireContext())

        // Set button as the list popup's anchor
        listPopupWindow.anchorView = listPopupWindowButton

        // Set list popup's content
        val items = Language.entries
        val adapter = ArrayAdapter(requireContext(), R.layout.item_menu_language, items)

        val popup = ListPopupWindow(requireContext()).apply {
            anchorView = binding.btnLanguage
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                selectedLanguage = items[position]
                binding.btnLanguage.text = selectedLanguage.toString()
                applyLanguage(selectedLanguage)
                dismiss()
            }
        }
        // Show list popup window on button click.
        binding.btnLanguage.setupTouchAction {
            popup.show()
        }


    }

    private fun createDialogCloseSession() {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(resources.getString(R.string.settings_close_session_title))
            .setMessage(resources.getString(R.string.settings_close_session_text))
            .setPositiveButton(resources.getString(R.string.btn_cancel)) { dialog, which -> }
            .setNegativeButton(resources.getString(R.string.settings_close_session_close)) { dialog, which ->
                settingsViewModel.signOut(requireContext())
            }
            .show()
    }

    private fun applyLanguage(language: Language) {
        val appLocale = LocaleListCompat.forLanguageTags(language.code)
        AppCompatDelegate.setApplicationLocales(appLocale)

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

    override fun onPause() {
        super.onPause()
        val isWeightKgMode = binding.modeToggleGroupWeight.checkedButtonId == R.id.btnWeightKg
        val isExerciseKgMode = binding.modeToggleGroupExercise.checkedButtonId == R.id.btnExerciseKg
        val isDarkMode = binding.btnDarkMode.isChecked
        val language = selectedLanguage

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val settings = UserSettings(
                    isDarkMode = isDarkMode,
                    isWeightKgMode = isWeightKgMode,
                    isExerciseKgMode = isExerciseKgMode,
                    language = language
                )
                settingsViewModel.saveUserSettingsData(settings)
            } catch (e: Exception) {
                Log.e("SettingsFragment", e.message.toString())
            }
        }
    }
}
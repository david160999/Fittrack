package com.cursointermedio.myapplication.ui.settings

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
import com.cursointermedio.myapplication.utils.extensions.UserDataUiSate
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.cursointermedio.myapplication.utils.extensions.showSnackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

// Fragmento para mostrar y modificar los ajustes del usuario (tema, idioma, unidades, perfil, sesión)
@AndroidEntryPoint
class SettingsFragment @Inject constructor() : Fragment() {

    // ViewModel de ajustes
    private val settingsViewModel: SettingsViewModel by viewModels()

    // ViewBinding para acceso seguro a las vistas
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Idioma seleccionado por el usuario
    private lateinit var selectedLanguage: Language

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()        // Inicializa componentes de UI
        initListeners() // Setea listeners y observadores de datos
    }

    // Inicializa la UI, por ejemplo el menú de idiomas
    private fun initUi() {
        setUpMenuLanguage()
    }

    // Inicializa listeners y observadores de los datos del ViewModel
    private fun initListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observa los datos de ajustes del usuario
                launch {
                    settingsViewModel.userSettingsData.collectLatest { state ->
                        when (state) {
                            is SettingsUiState.Error -> {
                                showSnackbar(binding.root.rootView, state.message, requireContext())
                            }
                            SettingsUiState.Loading -> { /* Puedes mostrar un loading si quieres */ }
                            is SettingsUiState.Success -> {
                                val userSettings = state.userSettings
                                // Actualiza los campos de la UI según los datos del usuario
                                binding.tvUserName.text = getString(R.string.home_user_name, userSettings.username)
                                binding.tvUserEmail.text = userSettings.email
                                binding.btnLanguage.text = state.userSettings.language.toString()
                                selectedLanguage = state.userSettings.language
                                // Unidades de peso (usuario)
                                if (userSettings.isWeightKgMode) {
                                    binding.modeToggleGroupWeight.check(R.id.btnWeightKg)
                                } else {
                                    binding.modeToggleGroupWeight.check(R.id.btnWeightLbs)
                                }
                                // Unidades de peso (ejercicio)
                                if (userSettings.isExerciseKgMode) {
                                    binding.modeToggleGroupExercise.check(R.id.btnExerciseKg)
                                } else {
                                    binding.modeToggleGroupExercise.check(R.id.btnExerciseLbs)
                                }
                            }
                        }
                    }
                }
                // Observa los datos básicos del usuario (foto, etc.)
                launch {
                    settingsViewModel.userData.collectLatest { state ->
                        when (state) {
                            is UserDataUiSate.Error -> {
                                showSnackbar(binding.root.rootView, state.message, requireContext())
                            }
                            UserDataUiSate.Loading -> { }
                            is UserDataUiSate.Success -> {
                                val user = state.userData
                                // Muestra la foto de perfil del usuario
                                Glide.with(requireContext())
                                    .load(user.photoUrl)
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_circle)
                                    .error(R.drawable.ic_circle)
                                    .into(binding.ivUserProfile)
                            }
                        }
                    }
                }
            }
        }

        // Listener para cerrar sesión
        binding.btnCloseSession.setupTouchAction {
            createDialogCloseSession()
        }

        // Listener para volver a Home
        binding.lyGoHome.setupTouchAction {
            findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
        }
    }

    // Configura el menú de selección de idioma usando ListPopupWindow
    private fun setUpMenuLanguage() {
        val listPopupWindowButton = binding.btnLanguage
        val listPopupWindow = ListPopupWindow(requireContext())
        listPopupWindow.anchorView = listPopupWindowButton

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
        // Muestra el popup al pulsar el botón de idioma
        binding.btnLanguage.setupTouchAction {
            popup.show()
        }
    }

    // Crea el diálogo de confirmación para cerrar sesión
    private fun createDialogCloseSession() {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(resources.getString(R.string.settings_close_session_title))
            .setMessage(resources.getString(R.string.settings_close_session_text))
            .setPositiveButton(resources.getString(R.string.btn_cancel)) { dialog, _ -> }
            .setNegativeButton(resources.getString(R.string.settings_close_session_close)) { dialog, _ ->
                settingsViewModel.signOut(requireContext())
            }
            .show()
    }

    // Aplica el idioma seleccionado a la aplicación
    private fun applyLanguage(language: Language) {
        val appLocale = LocaleListCompat.forLanguageTags(language.code)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    // Infla la vista del fragmento usando ViewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    // Limpia el binding para evitar memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Guarda los cambios en los ajustes si el usuario cambia de pantalla
    override fun onPause() {
        super.onPause()
        val state = settingsViewModel.userSettingsData.value
        if (state is SettingsUiState.Success) {
            val oldSettings = state.userSettings
            val isWeightKgMode = binding.modeToggleGroupWeight.checkedButtonId == R.id.btnWeightKg
            val isExerciseKgMode = binding.modeToggleGroupExercise.checkedButtonId == R.id.btnExerciseKg
            val language = selectedLanguage

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val settings = oldSettings.copy(
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
}
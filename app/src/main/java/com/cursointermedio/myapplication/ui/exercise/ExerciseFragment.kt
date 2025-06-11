package com.cursointermedio.myapplication.ui.exercise

import android.os.Bundle
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
import com.cursointermedio.myapplication.ui.routine.dialog.ExerciseDescriptionDialog
import com.cursointermedio.myapplication.ui.training.adapter.TrainingMenuAdapter
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExerciseFragment : Fragment() {

    // ViewModel propio del fragmento para manejar la lógica de ejercicios
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    // ViewBinding para acceder de forma segura a las vistas del fragmento
    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!

    // Argumentos de navegación recibidos para este fragmento
    private val args: ExerciseFragmentArgs by navArgs()

    // Referencia al ViewPager2 para navegar entre pestañas (objetivo/real)
    private lateinit var viewPager: ViewPager2

    // Se llama cuando la vista del fragmento ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()        // Inicializa la interfaz de usuario
        initListener()  // Inicializa los listeners y observadores
    }

    /**
     * Método para inicializar listeners y observadores de la UI y el ViewModel.
     */
    private fun initListener() {
        // Listener para el botón de opciones del detalle (puntos verticales)
        binding.ivPoints.setupTouchAction {
            createDetailOptionsMenu()
        }

        // Listener para el botón de retroceso
        binding.linearBack.setupTouchAction {
            findNavController().popBackStack()
        }

        // Listener para el cambio de página en el ViewPager
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        // Cambia al fragmento de Objetivo
                        exerciseViewModel.changeFragmentAdapter(0)
                    }
                    1 -> {
                        // Cambia al fragmento de Real
                        exerciseViewModel.changeFragmentAdapter(1)
                    }
                }
            }
        })

        // Observa los estados del ViewModel usando corrutinas y lifecycleScope
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observa cambios en el ejercicio seleccionado y actualiza el título
                launch {
                    exerciseViewModel.exercise.collectLatest { exercise ->
                        exercise?.let {
                            binding.tvTitle.text = exercise.getExerciseNameFromKey(requireContext())
                        }
                    }
                }
                // Observa el fragmento actual del adaptador y navega en el ViewPager
                launch {
                    exerciseViewModel.adapterFragment.collectLatest {
                        viewPager.setCurrentItem(it, true)
                    }
                }
            }
        }
    }

    /**
     * Muestra un menú emergente con opciones sobre los detalles del ejercicio.
     */
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

        // Adapter para las opciones del menú, con acciones según la opción seleccionada
        val adapter = TrainingMenuAdapter(items) { position ->
            when (position) {
                0 -> createExerciseDescriptionDialog(exerciseViewModel.exercise.value)
                1 -> exerciseViewModel.deleteLastDetail()
            }
            popupWindow.dismiss()
        }
        recyclerView.adapter = adapter

        // Configuración visual y comportamiento del popup
        popupWindow.apply {
            isFocusable = true
            isOutsideTouchable = true
            elevation = 12f
            isClippingEnabled = true
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            animationStyle = R.style.MenuTRainingPopupFadeAnimation
        }

        // Muestra el popup en la posición deseada respecto al icono de puntos
        popupWindow.showAsDropDown(binding.ivPoints, -420, 50)
    }

    /**
     * Inicializa la UI, configura el ViewPager2 y las pestañas.
     */
    private fun initUI() {
        val tabLayout = binding.tabLayout
        viewPager = binding.pager
        val pagerAdapter = ExercisePageAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = pagerAdapter
        viewPager.setCurrentItem(1, false) // Por defecto muestra la pestaña "REAL"

        // Conecta el TabLayout con el ViewPager2 y asigna los títulos de las pestañas
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

    /**
     * Muestra un diálogo con la descripción del ejercicio, si existe.
     */
    private fun createExerciseDescriptionDialog(exercise: ExerciseModel?) {
        if (exercise != null) {
            val dialog = ExerciseDescriptionDialog(
                exercise = exercise,
                context = requireContext()
            )
            dialog.show(parentFragmentManager, "dialog")
        }
    }

    /**
     * Infla la vista del fragmento y establece el binding.
     */
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
package com.cursointermedio.myapplication.ui.exercise.childFragments.realFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.databinding.FragmentRealBinding
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.ui.exercise.DetailUiState
import com.cursointermedio.myapplication.ui.exercise.ExerciseViewModel
import com.cursointermedio.myapplication.ui.exercise.adapter.detailsAdapter.RealDetailAdapter
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RealFragment : Fragment() {

    // ViewModel compartido con el fragmento padre para manejar la lógica de ejercicios
    private val exerciseViewModel: ExerciseViewModel by viewModels({ requireParentFragment() })

    // ViewBinding para acceder de forma segura a las vistas del fragmento
    private var _binding: FragmentRealBinding? = null
    private val binding get() = _binding!!

    // Adaptador para el RecyclerView de detalles reales
    private lateinit var adapter: RealDetailAdapter

    // Lista mutable para almacenar temporalmente detalles (no parece utilizarse activamente)
    private var detailResponse = mutableListOf<DetailModel>()

    // Se llama cuando la vista del fragmento ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()        // Inicializa la interfaz de usuario
        initListeners() // Inicializa los listeners y observadores
    }

    /**
     * Inicializa el RecyclerView y su adaptador.
     * Configura el layout y las acciones de los ítems.
     */
    private fun initUI() {
        adapter = RealDetailAdapter(
            onItemChanged = { detail ->
                // Callback para cuando se actualiza un detalle
                exerciseViewModel.updateDetailToRoutineExercise(detail)
            },
            onItemChangedFragment = { fragment ->
                // Callback para cambiar el fragmento desde el adaptador
                exerciseViewModel.changeFragmentAdapter(fragment)
            })

        binding.rvDetail.layoutManager = LinearLayoutManager(context)
        binding.rvDetail.adapter = adapter
        binding.rvDetail.isNestedScrollingEnabled = false // Evita conflictos de scroll anidado
    }

    /**
     * Inicializa los listeners de la UI y los coleccionadores de estados del ViewModel.
     * Observa los estados de detalles, estadísticas y notas.
     */
    private fun initListeners() {
        // Listener para añadir un nuevo detalle cuando se pulsa el botón "+"
        binding.cvPlus.setupTouchAction {
            exerciseViewModel.insertDetailToRoutineExercise()
        }

        // Observa los estados del ViewModel usando corrutinas y lifecycleScope
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observa la lista de detalles
                launch {
                    exerciseViewModel.detailList.collectLatest { state ->
                        when (state) {
                            is DetailUiState.Error -> {
                                // Manejar el error si es necesario
                            }
                            DetailUiState.Loading -> {
                                // Mostrar indicador de carga si es necesario
                            }
                            is DetailUiState.Success -> {
                                // Actualiza la lista del adaptador con los nuevos detalles
                                adapter.submitList(state.detailList)
                            }
                        }
                    }
                }
                // Observa las estadísticas del ejercicio y actualiza los TextView correspondientes
                launch {
                    exerciseViewModel.exerciseStatistics.collectLatest { statistics ->
                        binding.tvTonelaje.text = statistics.first.toString()
                        binding.tvResultErm.text = statistics.second.toString()
                    }
                }
                // Observa las notas y las muestra en el EditText si existen
                launch {
                    exerciseViewModel.notes.collectLatest { notes ->
                        notes?.let {
                            binding.etNotes.setText(notes)
                        }
                    }
                }
            }
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
        _binding = FragmentRealBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    /**
     * Al pausar el fragmento, actualiza los detalles y notas en el ViewModel.
     */
    override fun onPause() {
        super.onPause()

        // Actualiza la lista de detalles en el ViewModel
//        exerciseViewModel.updateDetailToRoutineExercise(exerciseViewModel.
//        detailResponseList.value)

        // Actualiza las notas si el campo no está vacío
        val notes = binding.etNotes.text.toString()
        if (notes.isNotEmpty()) {
            exerciseViewModel.updateNotesFromCrossRef(notes)
        }
    }
}
package com.cursointermedio.myapplication.ui.addExercise

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView   // ✅ BIEN, esta es la que corresponde al XML
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentAddexerciseBinding
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey
import com.cursointermedio.myapplication.ui.addExercise.adapterCategory.CategoryAdapter
import com.cursointermedio.myapplication.ui.addExercise.adapterExercise.AddExerciseAdapter
import com.cursointermedio.myapplication.ui.addExercise.adapterExercise.SelectedExerciseAdapter
import com.cursointermedio.myapplication.ui.addExercise.dialog.AddExerciseDialog
import com.cursointermedio.myapplication.ui.addExercise.dialog.RemoveExerciseDialog
import com.cursointermedio.myapplication.ui.routine.RoutineFragmentArgs
import com.cursointermedio.myapplication.ui.week.WeekFragmentDirections
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.cursointermedio.myapplication.utils.extensions.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddExerciseFragment : Fragment() {

    // ViewModel para manejar la lógica de negocio y datos
    private val addExerciseViewModel: AddExerciseViewModel by viewModels()

    // Binding para acceder a vistas de forma segura
    private var _binding: FragmentAddexerciseBinding? = null
    private val binding get() = _binding!!

    // Adaptadores para la lista de ejercicios, categorías y ejercicios seleccionados
    private lateinit var adapterExercise: AddExerciseAdapter
    private lateinit var adapterCategory: CategoryAdapter
    private lateinit var adapterSelected: SelectedExerciseAdapter

    // Estado reactivo con la lista de ejercicios seleccionados
    private val selectedExercises = MutableStateFlow<List<ExerciseModel>>(emptyList())

    // Lista original de ejercicios para buscar y filtrar
    private var originalList: List<ExerciseModel> = emptyList()

    // Lista de categorías disponibles
    private var categoryList: List<CategoryInfo> = emptyList()

    // Argumentos recibidos desde la navegación (por ejemplo, rutinaId)
    private val args: AddExerciseFragmentArgs by navArgs()
    private var routineId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar la vista con ViewBinding
        _binding = FragmentAddexerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtener rutinaId pasado en los argumentos
        routineId = args.routineId

        // Inicializar la UI y los listeners
        initUI()
        initListeners()
    }

    private fun initUI() {
        // Configurar adaptadores para listas
        setUpSelectedAdapter()
        setUpExerciseAdapter()
        setUpCategoryAdapter()
    }

    private fun initListeners() {
        // Botón para regresar al fragmento anterior
        binding.ivBack.setupTouchAction {
            findNavController().popBackStack()
        }

        // Botón para abrir diálogo para añadir nuevo ejercicio
        binding.ivPlusExercise.setupTouchAction {
            createDialogAdd()
        }

        // Botón para guardar la selección y regresar
        binding.tvNext.setupTouchAction {
            lifecycleScope.launch {
                // Insertar ejercicios seleccionados a la rutina
                addExerciseViewModel.insertExerciseToRoutine(routineId, selectedExercises.value)
                findNavController().popBackStack()
            }
        }

        // Configurar SearchView para buscar ejercicios por nombre
        binding.svExercise.apply {
            setOnClickListener {
                isIconified = false
                requestFocus()
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(findFocus(), InputMethodManager.SHOW_IMPLICIT)
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    // Filtrar lista original por texto introducido
                    val filteredText = newText.orEmpty()
                    val filteredList = originalList.filter { exercise ->
                        exercise.getExerciseNameFromKey(requireContext())
                            .contains(filteredText, ignoreCase = true)
                    }
                    // Actualizar lista visible en el adaptador
                    adapterExercise.submitList(filteredList)
                    return true
                }
            })
        }

        // Recoger estados de datos de ViewModel y actualizar UI reactivamente
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Recoger lista de ejercicios
                launch {
                    addExerciseViewModel.exerciseList.collectLatest { state ->
                        when (state) {
                            is AddExerciseUiState.Error -> showSnackbar(
                                binding.root,
                                state.message,
                                requireContext()
                            )

                            AddExerciseUiState.Loading -> { /* Opcional: mostrar progreso */
                            }

                            is AddExerciseUiState.Success -> {
                                // Guardar lista original y mostrarla
                                originalList = state.exercises
                                adapterExercise.submitList(state.exercises)
                            }
                        }
                    }
                }

                // Recoger lista de categorías
                launch {
                    addExerciseViewModel.categoryList.collectLatest { state ->
                        when (state) {
                            is CategoryUiState.Error -> showSnackbar(
                                binding.root,
                                state.message,
                                requireContext()
                            )

                            CategoryUiState.Loading -> { /* Opcional: mostrar progreso */
                            }

                            is CategoryUiState.Success -> {
                                // Guardar categorías y actualizar adaptador
                                categoryList = state.categoryList
                                adapterCategory.submitList(state.categoryList)
                            }
                        }
                    }
                }

                // Observar lista de ejercicios seleccionados para actualizar UI
                launch {
                    selectedExercises.collectLatest { exerciseList ->
                        // Actualizar adaptador de ejercicios seleccionados
                        adapterSelected.submitList(exerciseList)
                        // Mostrar u ocultar botón siguiente según si hay ejercicios seleccionados
                        binding.tvNext.visibility =
                            if (exerciseList.isNotEmpty()) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    private fun setUpSelectedAdapter() {
        // Adaptador para ejercicios seleccionados con callback para eliminar ejercicio
        adapterSelected = SelectedExerciseAdapter(
            onItemSelected = { exercise -> createDialogRemove(exercise) }
        )
        binding.lvAddExercises.layoutManager = LinearLayoutManager(context)
        binding.lvAddExercises.adapter = adapterSelected

        // Añadir separador vertical entre ítems
        binding.lvAddExercises.addItemDecoration(
            DividerItemDecoration(binding.rvExercise.context, LinearLayoutManager.VERTICAL)
        )
    }

    private fun setUpExerciseAdapter() {
        // Adaptador para lista de ejercicios con callback para añadir ejercicio seleccionado
        adapterExercise = AddExerciseAdapter(
            onItemSelected = { addExercise -> addToListAddExercise(addExercise) }
        )
        binding.rvExercise.layoutManager = LinearLayoutManager(context)
        binding.rvExercise.adapter = adapterExercise

        // Añadir separador vertical
        binding.rvExercise.addItemDecoration(
            DividerItemDecoration(binding.rvExercise.context, LinearLayoutManager.VERTICAL)
        )
    }

    private fun setUpCategoryAdapter() {
        // Adaptador para categorías con callback para selección de categoría
        adapterCategory = CategoryAdapter { categoryId, isSelected ->
            selectCategory(categoryId, isSelected)
        }
        binding.rvCategory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategory.adapter = adapterCategory
    }

    // Añade ejercicio a la lista de seleccionados si no está ya
    private fun addToListAddExercise(exercise: ExerciseModel) {
        if (!selectedExercises.value.contains(exercise)) {
            selectedExercises.value = selectedExercises.value + exercise
        }
    }

    // Elimina ejercicio de la lista de seleccionados
    private fun removeToListAddExercise(exercise: ExerciseModel) {
        selectedExercises.value = selectedExercises.value - exercise
    }

    // Cambia la lista de ejercicios mostrada según categoría seleccionada
    private fun selectCategory(categoryId: Long, isSelected: Boolean) {
        if (isSelected) {
            // Si categoría está deseleccionada, mostrar todos los ejercicios
            addExerciseViewModel.getAllExercise()
        } else {
            // Mostrar solo ejercicios de la categoría seleccionada
            addExerciseViewModel.getExercisesFromCategory(categoryId)
        }
    }

    // Muestra diálogo para añadir nuevo ejercicio
    private fun createDialogAdd() {
        val dialog = AddExerciseDialog(
            onSaveClickListener = { exercise ->
                lifecycleScope.launch {
                    addExerciseViewModel.insertExercise(exercise)
                }
            },
            categoryList
        )
        dialog.show(parentFragmentManager, "dialog")
    }

    // Muestra diálogo para confirmar eliminación de ejercicio seleccionado
    private fun createDialogRemove(exercise: ExerciseModel) {
        val dialog = RemoveExerciseDialog(
            onSaveClickListener = {
                lifecycleScope.launch {
                    removeToListAddExercise(exercise)
                }
            },
            exercise = exercise
        )
        dialog.show(parentFragmentManager, "dialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpiar binding para evitar fugas de memoria
        _binding = null
    }
}
package com.cursointermedio.myapplication.ui.routine.dialog

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.DialogExerciseDescriptionBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.getExerciseDescriptionFromKey
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// Diálogo de hoja inferior para mostrar la descripción de un ejercicio y acceso rápido a YouTube.
// Muestra el nombre y la descripción del ejercicio y permite buscar un tutorial en YouTube.
class ExerciseDescriptionDialog(
    private val exercise: ExerciseModel,   // Ejercicio a mostrar
    private val context: Context           // Contexto para acceder a recursos
) : BottomSheetDialogFragment() {

    // ViewBinding para inflar el layout y acceder a las vistas
    private var _binding: DialogExerciseDescriptionBinding? = null
    private val binding get() = _binding!!

    // Infla el layout del diálogo
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogExerciseDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Configuración inicial de la UI y listeners
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Establece fondo transparente y ajusta el teclado
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        initUi()
        initListeners()
    }

    // Inicializa listener para el ícono de YouTube: abre búsqueda del ejercicio en YouTube
    private fun initListeners() {
        binding.youtubeLogo.setupTouchAction {
            val query = "How to: ${exercise.getExerciseNameFromKey(context)}"
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/results?search_query=$query")
            )
            startActivity(intent)
        }
    }

    // Muestra el nombre y la descripción del ejercicio
    private fun initUi() {
        binding.exerciseTitle.text = exercise.getExerciseNameFromKey(context)
        binding.exerciseDescription.text = exercise.getExerciseDescriptionFromKey(context)
    }

    // Limpia el binding para evitar memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Usa un estilo personalizado para el BottomSheet
    override fun getTheme(): Int {
        return R.style.BottomSheetDialogAnimationStyle
    }

    // Ajusta la altura máxima del diálogo y su estado al abrir
    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            it.background = null
            // Cambia la altura máxima al 95% de la pantalla
            val layoutParams = it.layoutParams
            val displayMetrics = Resources.getSystem().displayMetrics
            val maxHeight = (displayMetrics.heightPixels * 0.95).toInt()

            layoutParams.height = maxHeight
            it.layoutParams = layoutParams
            behavior.isDraggable = true

            behavior.peekHeight = maxHeight  // Empieza con esa altura
            behavior.state = BottomSheetBehavior.STATE_EXPANDED // Abre expandido
        }
    }
}
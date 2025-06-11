package com.cursointermedio.myapplication.ui.routine.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.DialogRoutingBinding
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction

// Diálogo para crear o editar el nombre de una rutina. Se muestra en la parte inferior de la pantalla.
class RoutineDialog(
    private val onSaveClickListener: (String) -> Unit, // Callback cuando el usuario guarda la rutina (nombre)
    private var numRoutines: Int                       // Número actual de rutinas para sugerir nombre por defecto
) : DialogFragment() {

    // ViewBinding para acceder de forma segura a las vistas del layout
    private var _binding: DialogRoutingBinding? = null
    private val binding get() = _binding!!

    // Método principal para crear el diálogo personalizado
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Infla el layout del diálogo usando ViewBinding
        _binding = DialogRoutingBinding.inflate(layoutInflater)

        // Usa estilo personalizado y configura la vista
        val builder = AlertDialog.Builder(context, R.style.DialogAnimationStyle)
        builder.setView(binding.root)

        // Crea el diálogo y aplica estilos de ventana (fondo, posición, animación, etc)
        val dialog = builder.create().apply {
            setCanceledOnTouchOutside(true) // Permite cerrarlo tocando fuera
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setGravity(Gravity.BOTTOM)
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        // Inicializa la UI y listeners
        initUI()
        initListeners()

        return dialog
    }

    // Inicializa el hint del TextInputLayout con un nombre sugerido (por ejemplo "Rutina 3")
    private fun initUI() {
        val hint = getString(R.string.week_addRoutine_name, numRoutines + 1)
        binding.textInputLayout.hint = hint
    }

    // Configura el listener del botón guardar: si el campo está vacío, usa el hint como nombre
    private fun initListeners() {
        binding.cvSave.setupTouchAction {
            val name = binding.editTextRoutineName.text.toString().ifBlank {
                binding.textInputLayout.hint.toString()
            }
            onSaveClickListener(name)
            dialog?.dismiss()
        }
    }

    // Ajusta el tamaño del diálogo al ancho completo al mostrarse
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
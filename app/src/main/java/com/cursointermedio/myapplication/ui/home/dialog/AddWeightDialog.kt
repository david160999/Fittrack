package com.cursointermedio.myapplication.ui.home.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.DialogAddWeightBinding
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction

// Diálogo personalizado para añadir o editar el peso
class AddWeightDialog(
    // Callback opcional que se llama cuando el diálogo se cierra
    private val onDismissCallback: (() -> Unit)? = null,
    // Callback que se ejecuta al guardar el peso introducido
    private val onSaveClick: (Float) -> Unit,
    // Peso actual (si existe) para mostrar en el campo de texto
    private val weight: Float?
) : DialogFragment() {

    // ViewBinding para acceder a las vistas del diálogo de forma segura
    private var _binding: DialogAddWeightBinding? = null
    private val binding get() = _binding!!

    /**
     * Crea y configura el diálogo personalizado.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddWeightBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext(), R.style.DialogAnimationStyle)
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(true) // Permite cerrar tocando fuera del diálogo

        // Configuración visual del diálogo
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM) // Posiciona el diálogo en la parte inferior
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        initUi()         // Inicializa la UI del diálogo
        initListeners()  // Inicializa los listeners de los botones

        return dialog
    }

    /**
     * Llama al callback cuando el diálogo se cierra.
     */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback?.invoke()
    }

    /**
     * Inicializa la interfaz de usuario del diálogo.
     * Si existe un peso previo, lo muestra en el EditText y pone el foco.
     */
    private fun initUi() {
        if (weight != null) {
            binding.etAddWeight.setText(weight.toString())
        }
        binding.etAddWeight.requestFocus()
    }

    /**
     * Configura las acciones de los botones del diálogo.
     * Al guardar, llama al callback y cierra el diálogo si el texto no está vacío.
     */
    private fun initListeners() {
        binding.btnSaveWeight.setupTouchAction {
            val weight = binding.etAddWeight.text.toString()
            if (weight.isNotBlank()) {
                onSaveClick(weight.toFloat())
            }
            dialog?.dismiss()
        }
    }
}
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
import com.cursointermedio.myapplication.databinding.DialogAddNotesBinding
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction

// Diálogo personalizado para añadir o editar una nota
class AddNoteDialog(
    // Callback opcional para cuando el diálogo se cierra
    private val onDismissCallback: (() -> Unit)? = null,
    // Callback para guardar la nota introducida
    private val onSaveClick: (String) -> Unit,
    // Nota actual (si existe) para mostrar en el campo de texto
    private val notes: String?
) : DialogFragment() {

    // ViewBinding para acceder a las vistas del diálogo de forma segura
    private var _binding: DialogAddNotesBinding? = null
    private val binding get() = _binding!!

    /**
     * Crea y configura el diálogo personalizado.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddNotesBinding.inflate(layoutInflater)

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
     * Si existe una nota previa, la muestra en el EditText y pone el foco.
     */
    private fun initUi(){
        if (!notes.isNullOrBlank()){
            binding.etAddNote.setText(notes.toString())
        }
        binding.etAddNote.requestFocus()
    }

    /**
     * Configura las acciones de los botones del diálogo.
     * Al guardar, llama al callback y cierra el diálogo si el texto no está vacío.
     */
    private fun initListeners(){
        binding.btnSaveNotes.setupTouchAction {
            val notes = binding.etAddNote.text.toString()
            if (notes.isNotBlank()) {
                onSaveClick(notes)
            }
            dialog?.dismiss()
        }
    }
}
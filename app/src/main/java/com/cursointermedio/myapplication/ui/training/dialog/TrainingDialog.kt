package com.cursointermedio.myapplication.ui.training.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.DialogTrainingBinding
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction

// Diálogo para crear un nuevo bloque de entrenamiento. Muestra un EditText con hint sugerido y botones de cancelar/guardar.
class TrainingDialog(
    private val onSaveClickListener: (String) -> Unit, // Callback cuando el usuario guarda el bloque (nombre)
    private val newTrainingNum: String                  // Número o texto para sugerir el nombre por defecto
) : DialogFragment() {

    // ViewBinding para acceder a las vistas del layout
    private var _binding: DialogTrainingBinding? = null
    private val binding get() = _binding!!

    // Crea el diálogo personalizado
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogTrainingBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Hint sugerido en el EditText con el número de bloque nuevo
        binding.editText.hint = binding.root.context.getString(R.string.training_newBlock, newTrainingNum)

        // Botón cancelar: cierra el diálogo
        binding.btnCancel.setupTouchAction {
            dialog.dismiss()
        }

        // Botón guardar: si el campo está vacío, usa el hint como nombre
        binding.btnSave.setupTouchAction {
            if (binding.editText.text.isEmpty()) {
                onSaveClickListener.invoke(binding.editText.hint.toString())
            } else {
                onSaveClickListener.invoke(binding.editText.text.toString())
            }
            dialog.dismiss()
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
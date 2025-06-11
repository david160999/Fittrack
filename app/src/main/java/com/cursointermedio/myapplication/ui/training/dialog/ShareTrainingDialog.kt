package com.cursointermedio.myapplication.ui.training.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.DialogShareTrainingBinding
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction

// Diálogo para compartir un código de entrenamiento. Permite copiar el código al portapapeles y muestra confirmación.
class ShareTrainingDialog(
    private val trainingName: String,    // Nombre del entrenamiento a mostrar en el título
    private val code: String             // Código a compartir
) : DialogFragment() {

    // ViewBinding para acceder a las vistas del layout
    private var _binding: DialogShareTrainingBinding? = null
    private val binding get() = _binding!!

    // Crea el diálogo personalizado con animación, fondo y tamaño ajustados
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogShareTrainingBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context, R.style.DialogAnimationStyle)
        builder.setView(binding.root)

        val dialog = builder.create().apply {
            setCanceledOnTouchOutside(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setGravity(Gravity.BOTTOM)
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        initUi()
        initListeners()
        return dialog
    }

    // Inicializa la UI con el nombre y código a compartir
    private fun initUi() {
        binding.tvTitleSharingCode.text = getString(R.string.training_dialog_share, trainingName)
        binding.tvSharingCode.text = code
    }

    // Asigna listener al botón de copiar: copia código y muestra confirmación
    private fun initListeners() {
        binding.ivCopyTraining.setupTouchAction {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val clip = android.content.ClipData.newPlainText("label", code)
            clipboard.setPrimaryClip(clip)

            binding.tvSharingCodeCopied.visibility = View.VISIBLE
        }
    }

    // Ajusta el tamaño del diálogo al mostrarse
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
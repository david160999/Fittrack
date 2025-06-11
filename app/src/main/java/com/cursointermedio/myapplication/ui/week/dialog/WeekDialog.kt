package com.cursointermedio.myapplication.ui.week.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.cursointermedio.myapplication.databinding.DialogWeekBinding
import com.cursointermedio.myapplication.domain.useCase.CopyOption

// Diálogo para seleccionar la opción de copiado de una semana.
// Permite al usuario elegir entre copiar toda la semana, solo detalles o solo objetivos.

class WeekDialog(
    private val onSaveClickListener: (CopyOption?) -> Unit, // Callback con la opción seleccionada
) : DialogFragment() {

    private var _binding: DialogWeekBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogWeekBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Copiar toda la semana
        binding.tvAllWeek.setOnClickListener {
            onSaveClickListener.invoke(null)
            dialog.dismiss()
        }

        // Copiar todos los detalles de los ejercicios
        binding.tvAllExercise.setOnClickListener {
            onSaveClickListener.invoke(CopyOption.CopyAllDetails)
            dialog.dismiss()
        }

        // Copiar solo los objetivos
        binding.tvAllObjective.setOnClickListener {
            onSaveClickListener.invoke(CopyOption.CopyOnlyObjective)
            dialog.dismiss()
        }

        return dialog
    }
}
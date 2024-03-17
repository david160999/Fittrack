package com.cursointermedio.myapplication.ui.training.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.cursointermedio.myapplication.databinding.DialogTrainingBinding

class TrainingDialog(
    private val onSaveClickListener: (String) -> Unit,
    private val newTrainingNum: String
) : DialogFragment() {

    private var _binding: DialogTrainingBinding? = null
    private val binding get() = _binding!!


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogTrainingBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.editText.hint = binding.editText.hint.toString() + " " +newTrainingNum

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        binding.btnSave.setOnClickListener {
            onSaveClickListener.invoke(binding.editText.toString())
        }

        return dialog
    }
}
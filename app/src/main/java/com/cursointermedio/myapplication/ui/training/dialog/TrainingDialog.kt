package com.cursointermedio.myapplication.ui.training.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.DialogTrainingBinding
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction

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
        binding.editText.hint = binding.root.context.getString(R.string.training_newBlock, newTrainingNum)

        binding.btnCancel.setupTouchAction {
            dialog.dismiss()
        }

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
}
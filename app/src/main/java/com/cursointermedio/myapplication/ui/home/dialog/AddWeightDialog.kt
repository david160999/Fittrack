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
import com.cursointermedio.myapplication.databinding.DialogAddWeightBinding
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction


class AddWeightDialog(
    private val onDismissCallback: (() -> Unit)? = null,
    private val onSaveClick: (Float) -> Unit,
    private val weight: Float?
) : DialogFragment() {

    private var _binding: DialogAddWeightBinding? = null
    private val binding get() = _binding!!


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddWeightBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext(), R.style.DialogAnimationStyle)
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(true)

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        initUi()
        initListeners()

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback?.invoke()
    }

    private fun initUi() {
        if (weight != null) {
            binding.etAddWeight.setText(weight.toString())
        }
        binding.etAddWeight.requestFocus()

    }

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
package com.cursointermedio.myapplication.ui.training.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipboardManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.cursointermedio.myapplication.databinding.DialogDownloadTrainingBinding
import com.cursointermedio.myapplication.databinding.DialogTrainingBinding
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.cursointermedio.myapplication.R

class DownloadTrainingDialog(
    private val onSaveClickListener: (String) -> Unit
) : DialogFragment() {

    private var _binding: DialogDownloadTrainingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogDownloadTrainingBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context, R.style.DialogAnimationStyle)
        builder.setView(binding.root)

        val dialog = builder.create().apply {
            setCanceledOnTouchOutside(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setGravity(Gravity.BOTTOM)
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        initListeners()

        return dialog

    }

    private fun initListeners() {
        binding.btnImport.setupTouchAction {
            val code = binding.editTextCode.text.toString()
            onSaveClickListener.invoke(code)
            this.dialog?.dismiss()
        }

        binding.textInputLayout.setEndIconOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = clipboard.primaryClip

            if (clipData != null && clipData.itemCount > 0) {
                val pastedText = clipData.getItemAt(0).text.toString()
                binding.editTextCode.setText(pastedText)
            }
        }
        binding.editTextCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Aquí ya cambió el texto
                val hasText = !s.isNullOrEmpty()
                binding.btnImport.isEnabled = hasText
                if (hasText){
                    binding.btnImport.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.icons))
                }else{
                    binding.btnImport.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.Grey))
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
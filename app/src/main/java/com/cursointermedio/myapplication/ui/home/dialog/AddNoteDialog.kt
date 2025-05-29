package com.cursointermedio.myapplication.ui.home.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.DialogAddNotesBinding

class AddNoteDialog(
    private val onDismissCallback: (() -> Unit)? = null
) : DialogFragment() {

    private var _binding: DialogAddNotesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddNotesBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext(), R.style.DialogAnimationStyle)
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(true)

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        binding.etAddNote.requestFocus()

        return dialog
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback?.invoke()
    }

}
package com.cursointermedio.myapplication.ui.addExercise.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.DialogRemoveExerciseBinding
import com.cursointermedio.myapplication.databinding.DialogRoutingBinding
import com.cursointermedio.myapplication.databinding.DialogTrainingBinding
import com.cursointermedio.myapplication.databinding.FragmentWeekBinding

class RemoveExerciseDialog(
    private val onSaveClickListener: () -> Unit,

) : DialogFragment() {
    private var _binding: DialogRemoveExerciseBinding? = null
    private val binding get() = _binding!!


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRemoveExerciseBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context, R.style.dialog).setView(binding.root)

        val dialog = builder.create()

        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initUI()

        return dialog
    }

    private fun initUI() {
        binding.btnCancel.setOnClickListener {
            dismiss() // o cualquier otra acción
        }

        binding.btnEliminate.setOnClickListener {
            onSaveClickListener()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            800, // o 300.toPx(requireContext())
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }
}
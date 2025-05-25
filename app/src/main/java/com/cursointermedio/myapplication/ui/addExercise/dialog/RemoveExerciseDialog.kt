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
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey

class RemoveExerciseDialog(
    private val onSaveClickListener: () -> Unit,
    private val exercise: ExerciseModel

    ) : DialogFragment() {
    private var _binding: DialogRemoveExerciseBinding? = null
    private val binding get() = _binding!!


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRemoveExerciseBinding.inflate(layoutInflater)

        val builder =
            AlertDialog.Builder(context, R.style.DialogAnimationStyle).setView(binding.root)

        val dialog = builder.create()

        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initUi()
        initListeners()

        return dialog
    }

    private fun initUi() {
        val context = binding.root.context
        val exerciseName = exercise.getExerciseNameFromKey(context)

        binding.tvText.text = binding.root.context.getString(R.string.addExercise_dialogRemove_text, exerciseName)
    }

    private fun initListeners() {
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
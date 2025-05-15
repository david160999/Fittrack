package com.cursointermedio.myapplication.ui.routine.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
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
import com.cursointermedio.myapplication.databinding.DialogRoutingBinding
import com.cursointermedio.myapplication.databinding.DialogTrainingBinding
import com.cursointermedio.myapplication.databinding.FragmentWeekBinding
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction

class RoutineDialog(
    private val onSaveClickListener: (String) -> Unit,
    private val numRoutines: Int
) : DialogFragment() {
    private var _binding: DialogRoutingBinding? = null
    private val binding get() = _binding!!


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRoutingBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context, R.style.DialogAnimationStyle)
        builder.setView(binding.root)

        val dialog = builder.create().apply {
            setCanceledOnTouchOutside(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setGravity(Gravity.BOTTOM)
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        initUI()
        initListeners()



        return dialog
    }

    private fun initUI() {
        val hint = getString(R.string.week_addRoutine_name, numRoutines)
        binding.textInputLayout.hint = hint
    }

    private fun initListeners() {
        binding.cvSave.setupTouchAction {
            val name = binding.editTextRoutineName.text.toString().ifBlank {
                binding.textInputLayout.hint.toString()
            }
            onSaveClickListener(name)
            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
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

class RoutineDialog(
    private val onSaveClickListener: (String) -> Unit,
    private val weekId: Long,
    private val numRoutines: Int
) : DialogFragment() {
    private var _binding: DialogRoutingBinding? = null
    private val binding get() = _binding!!


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRoutingBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context, R.style.dialog)
        builder.setView(binding.root)
        val dialog = builder.create()


        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        initUI()
        setOnTouchListener(binding, dialog)


        return dialog
    }

    private fun initUI() {
        val hint = getString(R.string.week_addRoutine_name, numRoutines)
        binding.textInputLayout.hint = hint
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListener(binding: DialogRoutingBinding, dialog: Dialog) {
        binding.cvSave.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    binding.cvSave.alpha = 0.2F
                }

                android.view.MotionEvent.ACTION_MOVE -> {}
                android.view.MotionEvent.ACTION_UP -> {
                    binding.cvSave.alpha = 1F
                    val x = event.x
                    val y = event.y
                    if (x >= 0 && x <= v.width && y >= 0 && y <= v.height) {

                        val name = binding.editTextRoutineName.text.toString().ifBlank {
                            binding.textInputLayout.hint.toString()
                        }
                        onSaveClickListener(name)
                        dialog.dismiss()
                    }
                }

                android.view.MotionEvent.ACTION_CANCEL -> {
                    binding.cvSave.alpha = 1F
                }
            }
            true
        }
    }
}
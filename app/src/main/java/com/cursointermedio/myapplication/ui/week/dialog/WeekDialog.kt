package com.cursointermedio.myapplication.ui.week.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.cursointermedio.myapplication.databinding.DialogWeekBinding


class WeekDialog(
    private val onSaveClickListener: (String) -> Unit,
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
//      setupDialogProperties(dialog)


        binding.tvAllWeek.setOnClickListener {
            onSaveClickListener.invoke("CopyWeek")
            dialog.dismiss()

        }

        binding.tvAllExercise.setOnClickListener {
            onSaveClickListener.invoke("CopyWeekWithAll")
            dialog.dismiss()

        }

        binding.tvAllObjective.setOnClickListener {
            onSaveClickListener.invoke("CopyWeekWithObj")
            dialog.dismiss()

        }

        return dialog
    }
    /* private fun setupDialogProperties(alertDialog: AlertDialog) {
         val OFFSET_IN_PX = 140
         alertDialog.apply {
             window?.let { window ->
                 window.setDimAmount(0.1f) // from 0 for no dim to 1 for full dim.
                 window.attributes?.let { params ->
                     params.width = WindowManager.LayoutParams.MATCH_PARENT
                     params.height = WindowManager.LayoutParams.MATCH_PARENT
                     params.gravity = Gravity.TOP or Gravity.END
                     params.y = (OFFSET_IN_PX).toFloat().toInt()
                     params.x = 0
                 }
             }
             requestWindowFeature(Window.FEATURE_NO_TITLE)
         }
     }*/
}
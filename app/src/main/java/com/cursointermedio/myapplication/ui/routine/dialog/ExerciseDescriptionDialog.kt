package com.cursointermedio.myapplication.ui.routine.dialog

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.DialogAddCalendarBinding
import com.cursointermedio.myapplication.databinding.DialogExerciseDescriptionBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.getExerciseDescriptionFromKey
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ExerciseDescriptionDialog(
    private val exercise: ExerciseModel,
    private val context: Context
) : BottomSheetDialogFragment() {

    private var _binding: DialogExerciseDescriptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogExerciseDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        initUi()
        initListeners()

    }

    private fun initListeners() {
        binding.youtubeLogo.setupTouchAction {
            val query = "How to: ${exercise.getExerciseNameFromKey(context)}"
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/results?search_query=$query")
            )
            startActivity(intent)
        }
    }

    private fun initUi() {
        binding.exerciseTitle.text = exercise.getExerciseNameFromKey(context)
        binding.exerciseDescription.text = exercise.getExerciseDescriptionFromKey(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogAnimationStyle
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            it.background = null
            // Cambiar altura máxima a, por ejemplo, el 80% de la pantalla
            val layoutParams = it.layoutParams
            val displayMetrics = Resources.getSystem().displayMetrics
            val maxHeight = (displayMetrics.heightPixels * 0.95).toInt()

            layoutParams.height = maxHeight
            it.layoutParams = layoutParams
            behavior.isDraggable = true

            behavior.peekHeight = maxHeight  // para que empiece con esa altura

            behavior.state = BottomSheetBehavior.STATE_EXPANDED // para abrir expandido si querés
        }
    }
}
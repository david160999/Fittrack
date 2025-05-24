package com.cursointermedio.myapplication.ui.exercise.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.ItemExerciseBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey
import com.cursointermedio.myapplication.ui.routine.ExerciseMenuActions
import com.cursointermedio.myapplication.ui.training.adapter.TrainingMenuAdapter
import com.cursointermedio.myapplication.utils.extensions.isItemBelowThreshold
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.cursointermedio.myapplication.utils.extensions.setupTouchActionRecyclerView

class ExerciseViewHolder(
    private val binding: ItemExerciseBinding
) : RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("ClickableViewAccessibility", "PrivateResource")
    fun bind(
        exercise: ExerciseModel,
        onItemSelected: (Long) -> Unit,
        menuActions: ExerciseMenuActions
    ) {
        val context = binding.root.context

        binding.tvTitle.text = exercise.getExerciseNameFromKey(context)
        binding.tvNumTrainings.text =
            context.getString(R.string.routine_count_exercise, exercise.detailCount)

        binding.viewContainerTraining.setupTouchActionRecyclerView {
            if (exercise.id != null) {
                onItemSelected(exercise.id)
            }
        }
        binding.ivExerciseOptions.setupTouchAction {
            showMenu(
                binding.root,
                context,
                exercise,
                menuActions
            )
        }

    }

    private fun showMenu(
        view: View,
        context: Context,
        exercise: ExerciseModel,
        menuActions: ExerciseMenuActions
    ) {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu_training, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val recyclerView = popupView.findViewById<RecyclerView>((R.id.rvTrainingMenu))
        recyclerView.layoutManager = LinearLayoutManager(context)

        val items = listOf(
            ContextCompat.getString(context, R.string.routine_menuOption1),
            ContextCompat.getString(context, R.string.routine_menuOption2),
        )

        val adapter = TrainingMenuAdapter(items) { position ->
            when (position) {
                0 -> menuActions.onDescription(exercise)
                1 -> menuActions.onEliminate(exercise)
            }
            popupWindow.dismiss()
        }
        recyclerView.adapter = adapter

        popupWindow.apply {
            isFocusable = true
            isOutsideTouchable = true
            elevation = 12f
            isClippingEnabled = true
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            animationStyle = R.style.MenuTRainingPopupFadeAnimation
        }

        if (isItemBelowThreshold(binding.root)) {
            popupWindow.showAsDropDown(view, 450, -630)
        } else {
            popupWindow.showAsDropDown(view, 450, -30)
        }

    }


}


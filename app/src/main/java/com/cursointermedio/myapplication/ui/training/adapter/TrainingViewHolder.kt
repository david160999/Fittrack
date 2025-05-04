package com.cursointermedio.myapplication.ui.training.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.data.database.entities.TrainingsWithWeekAndRoutineCounts
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.databinding.ItemWeekBinding
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.toDomain
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction


class TrainingViewHolder(
    private val binding: ItemTrainingBinding, private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    var isChangeName = false
    val editText: EditText = binding.root.findViewById(R.id.editTextTitleItemTraining)

    @SuppressLint("ClickableViewAccessibility")
    fun bind(
        trainingItemResponse: TrainingsWithWeekAndRoutineCounts,
        onItemSelected: (Long) -> Unit,
        menuActions: TrainingMenuActions
    ) {
        val context = binding.root.context

        val numWeeks = trainingItemResponse.numWeeks
        val numRoutines = trainingItemResponse.numRoutines

        binding.tvTitle.text = trainingItemResponse.training.name
        binding.tvNumTrainings.text = context.getString(R.string.training_numDays, numRoutines)
        binding.tvNumCurrentsWeeks.text = context.getString(R.string.training_numWeeks, numWeeks)

        binding.root.setupTouchAction() {
            onItemSelected(trainingItemResponse.training.trainingId!!)
        }

        binding.ivTrainingOptions.setupTouchAction {
            showMenu(
                binding.root,
                context,
                trainingItemResponse,
                menuActions
            )
        }

    }

    private fun showMenu(
        view: View,
        context: Context,
        trainingItemResponse: TrainingsWithWeekAndRoutineCounts,
        menuActions: TrainingMenuActions
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
            ContextCompat.getString(context, R.string.training_menuOption1),
            ContextCompat.getString(context, R.string.training_menuOption2),
            ContextCompat.getString(context, R.string.training_menuOption3),
            ContextCompat.getString(context, R.string.training_menuOption4),
        )

        val adapter = TrainingMenuAdapter(items) { position ->
            when (position) {
                0 -> changeName(
                    trainingItemResponse.training.toDomain(),
                    menuActions
                )

                1 -> menuActions.onCopy(trainingItemResponse)
                2 -> menuActions.onShare(trainingItemResponse.training.toDomain())
                3 -> menuActions.onEliminate(trainingItemResponse.training.toDomain())
            }
            popupWindow.dismiss()
        }

        recyclerView.adapter = adapter
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 24f
        popupWindow.isClippingEnabled = true
        popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        popupWindow.showAsDropDown(view, 450, -30)
    }


    private fun changeName(
        training: TrainingModel,
        menuActions: TrainingMenuActions
    ) {
        activateLayoutChangeName()

        binding.ivTrainingSave.setupTouchAction {
            val name = binding.editTextTitleItemTraining.text.toString()

            if (name.isNotBlank()) {
                training.name = name
                menuActions.onChangeName(training)
            }
            disableLayoutChangeName()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun activateLayoutChangeName() {
        binding.root.setOnTouchListener(null)

        binding.root.setOnClickListener {
            binding.editTextTitleItemTraining.requestFocus()
            val imm =
                editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.editTextTitleItemTraining, InputMethodManager.SHOW_IMPLICIT)
        }


        this.isChangeName = true

        binding.ivTrainingOptions.visibility = View.GONE
        binding.ivTrainingSave.visibility = View.VISIBLE

        val name = binding.tvTitle.text
        binding.editTextTitleItemTraining.hint = name

        binding.tvTitle.visibility = View.GONE
        binding.editTextTitleItemTraining.visibility = View.VISIBLE

        changeLayout()
    }

    private fun disableLayoutChangeName() {

        this.isChangeName = false

        binding.ivTrainingOptions.visibility = View.VISIBLE
        binding.ivTrainingSave.visibility = View.GONE

        binding.editTextTitleItemTraining.text.clear()
        binding.editTextTitleItemTraining.clearFocus()

        binding.tvTitle.visibility = View.VISIBLE
        binding.editTextTitleItemTraining.visibility = View.GONE

        changeLayout()
    }

    private fun changeLayout() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.clItemTraining)

        constraintSet.clear(R.id.lyDays, ConstraintSet.TOP)

        if (binding.tvTitle.visibility == View.GONE) {
            constraintSet.connect(
                R.id.lyDays,
                ConstraintSet.TOP,
                R.id.editTextTitleItemTraining,
                ConstraintSet.BOTTOM,
            )


        } else {
            constraintSet.connect(
                R.id.lyDays,
                ConstraintSet.TOP,
                R.id.tvTitle,
                ConstraintSet.BOTTOM,
            )
        }
        constraintSet.applyTo(binding.clItemTraining)

    }
}



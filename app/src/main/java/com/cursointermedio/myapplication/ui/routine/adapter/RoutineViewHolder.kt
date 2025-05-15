package com.cursointermedio.myapplication.ui.routine.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ExpandableListView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.ui.training.adapter.TrainingMenuAdapter
import com.cursointermedio.myapplication.ui.week.adapter.ExpandableListAdapter
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.cursointermedio.myapplication.utils.extensions.setupTouchActionRecyclerView

class RoutineViewHolder(
    private val binding: ItemTrainingBinding,
) :
    RecyclerView.ViewHolder(binding.root) {
    private lateinit var numWeeks: String
    private lateinit var numRoutines: String

    private val editText: EditText = binding.root.findViewById(R.id.editTextTitleItemTraining)


    fun bind(
        routine: RoutineModel,
        onItemSelected: (Long) -> Unit,
        menuActions: RoutineMenuActions
    ) {
        val context = binding.root.context

        binding.tvTitle.text = routine.name
        binding.tvNumTrainings.text = routine.exerciseCount.toString()
        if (routine.date.isNullOrBlank()) {
            binding.tvNumCurrentsWeeks.text = ContextCompat.getString(context, R.string.week_date_routine)
        } else {
            binding.tvNumCurrentsWeeks.text = routine.date
        }

        binding.root.setupTouchActionRecyclerView {
            onItemSelected(routine.routineId!!)
        }

        binding.ivTrainingOptions.setupTouchAction {
            showMenu(
                binding.root,
                context,
                routine,
                menuActions,
                onItemSelected
            )
        }
    }


    private fun showMenu(
        view: View,
        context: Context,
        routine: RoutineModel,
        menuActions: RoutineMenuActions,
        onItemSelected: (Long) -> Unit
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
            ContextCompat.getString(context, R.string.training_menuOption4),
        )

        val adapter = TrainingMenuAdapter(items) { position ->
            when (position) {
                0 -> changeName(
                    routine,
                    menuActions,
                    onItemSelected
                )

                1 -> menuActions.onCopy(routine)
                2 -> menuActions.onEliminate(routine)
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

        if (isItemBelowThreshold()) {
            popupWindow.showAsDropDown(view, 450, -630)
        } else {
            popupWindow.showAsDropDown(view, 450, -30)
        }


    }

    private fun isItemBelowThreshold(): Boolean {
        val location = IntArray(2)
        binding.root.getLocationOnScreen(location)

        val itemTop = location[1] // Coordenada Y del top del item
        val itemBottom = itemTop + binding.root.height // Coordenada Y del bottom del item

        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val percentageThreshold = 0.75f

        val threshold = screenHeight * percentageThreshold

        // Verificar si el borde inferior del item ha superado el umbral (80%)
        return if (itemBottom > threshold) {
            true
        } else {
            false
        }
    }

    private fun changeName(
        routine: RoutineModel,
        menuActions: RoutineMenuActions,
        onItemSelected: (Long) -> Unit
    ) {
        activateLayoutChangeName()

        binding.editTextTitleItemTraining.requestFocus()
        ViewCompat.getWindowInsetsController(binding.editTextTitleItemTraining)
            ?.show(WindowInsetsCompat.Type.ime())

        binding.ivTrainingSave.setOnClickListener {
            val name = binding.editTextTitleItemTraining.text.toString()

            if (name.isNotBlank()) {
                routine.name = name
                menuActions.onChangeName(routine)
                binding.tvTitle.text = name
            }
            disableLayoutChangeName()
            binding.root.setupTouchAction() {
                onItemSelected(routine.routineId!!)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun activateLayoutChangeName() {
        binding.root.setOnTouchListener(null)

        binding.root.setOnClickListener {
            editText.post {
                if (!editText.hasFocus()) {
                    editText.requestFocus() // make sure it's focused
                    ViewCompat.getWindowInsetsController(editText)
                        ?.show(WindowInsetsCompat.Type.ime())
                }
            }
        }

        binding.ivTrainingOptions.visibility = View.GONE
        binding.ivTrainingSave.visibility = View.VISIBLE

        val name = binding.tvTitle.text
        binding.editTextTitleItemTraining.hint = name

        binding.tvTitle.visibility = View.GONE
        binding.editTextTitleItemTraining.visibility = View.VISIBLE

        changeLayout()
    }

    private fun disableLayoutChangeName() {

        binding.ivTrainingOptions.visibility = View.VISIBLE
        binding.ivTrainingSave.visibility = View.GONE

        binding.editTextTitleItemTraining.text.clear()
        binding.editTextTitleItemTraining.clearFocus()
        ViewCompat.getWindowInsetsController(editText)
            ?.hide(WindowInsetsCompat.Type.ime())

        binding.tvTitle.visibility = View.VISIBLE
        binding.editTextTitleItemTraining.visibility = View.INVISIBLE

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
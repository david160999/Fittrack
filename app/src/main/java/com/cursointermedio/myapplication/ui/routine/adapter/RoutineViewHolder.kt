package com.cursointermedio.myapplication.ui.routine.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
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
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.cursointermedio.myapplication.utils.extensions.setupTouchActionRecyclerView

// ViewHolder para un ítem de Routine en el RecyclerView.
// Permite mostrar información, editar el nombre, y mostrar un menú contextual con acciones.
class RoutineViewHolder(
    private val binding: ItemTrainingBinding,
) : RecyclerView.ViewHolder(binding.root) {

    // EditText para cambiar el nombre de la rutina
    private val editText: EditText = binding.root.findViewById(R.id.editTextTitleItemTraining)

    // Asocia los datos de la rutina y listeners de acciones a la vista
    fun bind(
        routine: RoutineModel,
        onItemSelected: (Long) -> Unit,
        menuActions: RoutineMenuActions
    ) {
        val context = binding.root.context

        // Muestra nombre, cantidad de ejercicios y fecha/semana
        binding.tvTitle.text = routine.name
        binding.tvNumTrainings.text = routine.exerciseCount.toString()
        if (routine.date.isNullOrBlank()) {
            binding.tvNumCurrentsWeeks.text = ContextCompat.getString(context, R.string.week_date_routine)
        } else {
            binding.tvNumCurrentsWeeks.text = routine.date
        }

        // Acción al tocar el card principal: selecciona el ítem
        binding.root.setupTouchActionRecyclerView {
            onItemSelected(routine.routineId!!)
        }

        // Acción para abrir el menú contextual (editar, copiar, eliminar)
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

    // Muestra el menú contextual de opciones de la rutina (cambiar nombre, copiar, eliminar)
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

        // Opciones: Cambiar nombre, Copiar, Eliminar
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

        // Decide posición del menú según su lugar en la pantalla
        if (isItemBelowThreshold()) {
            popupWindow.showAsDropDown(view, 450, -630)
        } else {
            popupWindow.showAsDropDown(view, 450, -30)
        }
    }

    // Verifica si el ítem está cerca del borde inferior de la pantalla para evitar que el menú salga fuera de la vista
    private fun isItemBelowThreshold(): Boolean {
        val location = IntArray(2)
        binding.root.getLocationOnScreen(location)

        val itemTop = location[1] // Coordenada Y top
        val itemBottom = itemTop + binding.root.height // Coordenada Y bottom

        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val percentageThreshold = 0.75f

        val threshold = screenHeight * percentageThreshold

        // Devuelve true si el bottom del ítem supera el 75% de la pantalla
        return itemBottom > threshold
    }

    // Habilita la edición del nombre de la rutina
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

    // Cambia la UI para mostrar el EditText y ocultar el título, y oculta el menú de opciones
    @SuppressLint("ClickableViewAccessibility")
    private fun activateLayoutChangeName() {
        binding.root.setOnTouchListener(null)

        binding.root.setOnClickListener {
            editText.post {
                if (!editText.hasFocus()) {
                    editText.requestFocus()
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

    // Restaura la UI al estado normal y oculta el teclado
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

    // Cambia la relación de constraints según si está visible el EditText o el título
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
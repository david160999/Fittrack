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

    /**
     * Enlaza los datos del ejercicio con la vista.
     * @param exercise Modelo de datos del ejercicio a mostrar.
     * @param onItemSelected Callback cuando se selecciona un ejercicio.
     * @param menuActions Acciones disponibles desde el menú contextual del ejercicio.
     */
    @SuppressLint("ClickableViewAccessibility", "PrivateResource")
    fun bind(
        exercise: ExerciseModel,
        onItemSelected: (Long) -> Unit,
        menuActions: ExerciseMenuActions
    ) {
        val context = binding.root.context

        // Muestra el nombre del ejercicio (usando una función para obtenerlo según clave)
        binding.tvTitle.text = exercise.getExerciseNameFromKey(context)
        // Muestra el número de entrenamientos asociados
        binding.tvNumTrainings.text = context.getString(R.string.routine_count_exercise, exercise.detailCount)

        // Configura el listener para seleccionar el ejercicio al tocar el contenedor
        binding.viewContainerTraining.setupTouchActionRecyclerView {
            if (exercise.id != null) {
                onItemSelected(exercise.id)
            }
        }
        // Configura el listener para mostrar el menú contextual al tocar el icono de opciones
        binding.ivExerciseOptions.setupTouchAction {
            showMenu(
                binding.root,
                context,
                exercise,
                menuActions
            )
        }
    }

    /**
     * Muestra un menú contextual emergente con opciones para el ejercicio.
     * @param view Vista ancla para el popup.
     * @param context Contexto de la aplicación.
     * @param exercise Ejercicio asociado al menú.
     * @param menuActions Acciones disponibles en el menú.
     */
    private fun showMenu(
        view: View,
        context: Context,
        exercise: ExerciseModel,
        menuActions: ExerciseMenuActions
    ) {
        // Infla la vista para el popup del menú
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu_training, null)

        // Crea el PopupWindow con la vista inflada y parámetros de tamaño/configuración
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        // Configura el RecyclerView del popup con un layout vertical
        val recyclerView = popupView.findViewById<RecyclerView>((R.id.rvTrainingMenu))
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Define las opciones del menú a mostrar (por ejemplo: descripción y eliminar)
        val items = listOf(
            ContextCompat.getString(context, R.string.routine_menuOption1),
            ContextCompat.getString(context, R.string.routine_menuOption2),
        )

        // Adapter para las opciones del menú, con acciones según la opción seleccionada
        val adapter = TrainingMenuAdapter(items) { position ->
            when (position) {
                0 -> menuActions.onDescription(exercise) // Acción para mostrar descripción
                1 -> menuActions.onEliminate(exercise)   // Acción para eliminar ejercicio
            }
            popupWindow.dismiss() // Cierra el popup tras seleccionar una opción
        }
        recyclerView.adapter = adapter

        // Configuración visual y comportamiento del popup
        popupWindow.apply {
            isFocusable = true
            isOutsideTouchable = true
            elevation = 12f
            isClippingEnabled = true
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            animationStyle = R.style.MenuTRainingPopupFadeAnimation
        }

        // Ajusta la posición del popup según el espacio disponible en pantalla
        if (isItemBelowThreshold(binding.root)) {
            popupWindow.showAsDropDown(view, 450, -630)
        } else {
            popupWindow.showAsDropDown(view, 450, -30)
        }
    }
}


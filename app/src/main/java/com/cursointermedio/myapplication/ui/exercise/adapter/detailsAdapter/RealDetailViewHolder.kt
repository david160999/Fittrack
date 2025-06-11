package com.cursointermedio.myapplication.ui.exercise.adapter.detailsAdapter

import android.annotation.SuppressLint
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.ItemDetailsBinding
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction

class RealDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    // Enlaza la vista con el ViewBinding generado para el layout del ítem
    private val binding = ItemDetailsBinding.bind(view)

    /**
     * Método principal para enlazar los datos al ViewHolder.
     * @param detail Modelo con los detalles a mostrar/editar.
     * @param onItemChanged Callback que se llama cuando se modifica algún campo.
     * @param onItemChangedFragment Callback para acciones específicas del fragment.
     */
    @SuppressLint("ClickableViewAccessibility", "PrivateResource", "SetTextI18n")
    fun bind(
        detail: DetailModel,
        onItemChanged: (DetailModel) -> Unit,
        onItemChangedFragment: (Int) -> Unit
    ) {
        // Inicializa la UI según el estado del detalle
        initUi(detail, onItemChangedFragment)
        // Inicializa los listeners de los campos editables
        initListeners(detail, onItemChanged)
    }

    /**
     * Inicializa la interfaz de usuario según los valores actuales del objeto.
     * Si los campos objetivos están cargados, los muestra, si no muestra los editables.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initUi(detail: DetailModel, onItemChangedFragment: (Int) -> Unit) {
        // Muestra los datos objetivo si existen, oculta los editables
        if (detail.objWeight != null && detail.objReps != null && detail.objRpe != null) {
            binding.tvCardView1.visibility = View.VISIBLE
            binding.ivCardView1.visibility = View.GONE
            // Muestra el resumen con formato "peso x repeticiones @ rpe"
            binding.tvCardView1.text = "${detail.objWeight} x ${detail.objReps} @ ${detail.objRpe}"

            binding.ivCardView1.visibility = View.GONE
            binding.tvCardView1.visibility = View.VISIBLE

            // Deshabilita acciones táctiles si el dato es objetivo
            binding.cv1.setOnTouchListener(null)
        } else {
            // Si faltan datos objetivo, muestra los iconos de edición
            binding.tvCardView1.visibility = View.GONE
            binding.ivCardView1.visibility = View.VISIBLE

            // Permite acción táctil para cambiar de ViewPager
            binding.cv1.setupTouchAction {
                onItemChangedFragment(0)
            }
        }

        // Rellena los EditText con los valores existentes (si existen)
        detail.realWeight?.let { binding.etWeight.setText(it.toString()) }
        detail.realReps?.let { binding.etReps.setText(it.toString()) }
        detail.realRpe?.let { binding.etRpe.setText(it.toString()) }
    }

    /**
     * Añade listeners a los EditText para detectar cambios y notificar mediante el callback.
     */
    private fun initListeners(detail: DetailModel, onItemChanged: (DetailModel) -> Unit) {
        var currentDetail = detail

        // Listener para cambios en el campo de peso real
        binding.etWeight.doAfterTextChanged { text ->
            val newWeight = text.toString().toIntOrNull()
            if (newWeight != currentDetail.realWeight) {
                currentDetail = currentDetail.copy(realWeight = newWeight)
                onItemChanged(currentDetail)
            }
        }

        // Listener para cambios en el campo de repeticiones reales
        binding.etReps.doAfterTextChanged { text ->
            val newReps = text.toString().toIntOrNull()
            if (newReps != currentDetail.realReps) {
                currentDetail = currentDetail.copy(realReps = newReps)
                onItemChanged(currentDetail)
            }
        }

        // Listener para cambios en el campo de RPE real
        binding.etRpe.doAfterTextChanged { text ->
            val newRpe = text.toString().toIntOrNull()
            if (newRpe != currentDetail.realRpe) {
                currentDetail = currentDetail.copy(realRpe = newRpe)
                onItemChanged(currentDetail)
            }
        }
    }
}
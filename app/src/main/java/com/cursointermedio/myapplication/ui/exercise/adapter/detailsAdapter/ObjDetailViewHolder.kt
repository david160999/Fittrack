package com.cursointermedio.myapplication.ui.exercise.adapter.detailsAdapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.ItemObjDetailsBinding
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction

class ObjDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    // Enlaza la vista con el ViewBinding generado para el layout del ítem
    private val binding = ItemObjDetailsBinding.bind(view)

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
     * Si los campos reales están cargados, los muestra, si no muestra los editables.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initUi(detail: DetailModel, onItemChangedFragment: (Int) -> Unit) {
        // Muestra los datos reales si existen, oculta los editables
        if (detail.realWeight != null && detail.realReps != null && detail.realRpe != null) {
            binding.tvCardView1.visibility = View.VISIBLE
            binding.ivCardView1.visibility = View.GONE
            // Muestra el resumen con formato "peso x repeticiones @ rpe"
            binding.tvCardView1.text =
                "${detail.realWeight} x ${detail.realReps} @ ${detail.realRpe}"

            binding.ivCardView1.visibility = View.GONE
            binding.tvCardView1.visibility = View.VISIBLE

            // Deshabilita acciones táctiles si el dato es real
            binding.cv4.setOnTouchListener(null)
        } else {
            // Si faltan datos reales, muestra los iconos de edición
            binding.tvCardView1.visibility = View.GONE
            binding.ivCardView1.visibility = View.VISIBLE

            // Permite acción táctil para cambiar de ViewPager
            binding.cv4.setupTouchAction {
                onItemChangedFragment(1)
            }
        }

        // Rellena los EditText con los valores existentes (si existen)
        binding.etWeight.setText(detail.objWeight?.toString() ?: "")
        binding.etReps.setText(detail.objReps?.toString() ?: "")
        binding.etRpe.setText(detail.objRpe?.toString() ?: "")
    }

    /**
     * Añade listeners a los EditText para detectar cambios y notificar mediante el callback.
     */
    private fun initListeners(detail: DetailModel, onItemChanged: (DetailModel) -> Unit) {
        var currentDetail = detail

        // Listener para cambios en el campo de peso
        binding.etWeight.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val newWeight = (v as EditText).text.toString().toIntOrNull()
                if (newWeight != currentDetail.objWeight) {
                    currentDetail = currentDetail.copy(objWeight = newWeight)
                    onItemChanged(currentDetail)
                }
            }
        }

        // Listener para cambios en el campo de repeticiones
        binding.etReps.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val newReps = (v as EditText).text.toString().toIntOrNull()
                if (newReps != currentDetail.objReps) {
                    currentDetail = currentDetail.copy(objReps = newReps)
                    onItemChanged(currentDetail)
                }
            }
        }

        // Listener para cambios en el campo de RPE
        binding.etRpe.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val newRpe = (v as EditText).text.toString().toIntOrNull()
                if (newRpe != currentDetail.objRpe) {
                    currentDetail = currentDetail.copy(objRpe = newRpe)
                    onItemChanged(currentDetail)
                }
            }
        }
    }
}

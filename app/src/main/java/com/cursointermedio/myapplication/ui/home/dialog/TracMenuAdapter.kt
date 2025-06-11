package com.cursointermedio.myapplication.ui.home.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.google.android.material.divider.MaterialDivider

// Adaptador para el menú de selección de Trac (BottomSheet), muestra una lista de opciones.
// Llama a un callback cuando el usuario selecciona una opción.
class TracMenuAdapter(
    private val items: List<String>,                  // Lista de opciones a mostrar
    private val onClick: (Int) -> Unit                // Callback de selección, recibe el índice
) : RecyclerView.Adapter<TracMenuAdapter.ViewHolder>() {

    // ViewHolder para cada opción del menú
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.tvNameMenuTrac)
        val divider: MaterialDivider = view.findViewById(R.id.diverMenuTrac)

        init {
            // Llama al callback cuando se hace click en el item
            view.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClick(position)
                }
            }
        }
    }

    // Infla la vista de cada ítem del menú
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_trac, parent, false)
        return ViewHolder(view)
    }

    // Asigna los datos y estilos a cada ítem
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val isLast = position == items.lastIndex

        // Texto de la opción
        holder.text.text = items[position]

        // Visibilidad del divisor: oculto si es el último elemento
        holder.divider.visibility = if (isLast) View.GONE else View.VISIBLE

        // Fondo diferente según la posición (primero, medio, último)
        val backgroundRes = when (position) {
            0 -> R.drawable.item_bg_first_menu_training
            items.lastIndex -> R.drawable.item_bg_last_menu_training
            else -> R.drawable.item_bg_middle_menu_training
        }
        holder.itemView.background = ContextCompat.getDrawable(context, backgroundRes)

        // Descripción de accesibilidad
        holder.text.contentDescription = "Training menu option ${items[position]}"
    }

    // Devuelve el número de elementos en la lista
    override fun getItemCount() = items.size
}
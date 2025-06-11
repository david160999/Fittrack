package com.cursointermedio.myapplication.ui.training.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.google.android.material.divider.MaterialDivider

class TrainingMenuAdapter(
    private val items: List<String>,            // Opciones del menú (texto)
    private val onClick: (Int) -> Unit          // Acción al hacer click en una opción (por posición)
) : RecyclerView.Adapter<TrainingMenuAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.tvNameMenuTraining)
        val divider: MaterialDivider = view.findViewById(R.id.diverMenuTraining)

        init {
            view.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_training, parent, false)
        return ViewHolder(view)
    }

    // Asocia datos y estilos a cada ítem del menú
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val isLast = position == items.lastIndex

        // Texto y color: la última opción es roja (típicamente "Eliminar")
        holder.text.text = items[position]
        holder.text.setTextColor(
            ContextCompat.getColor(context, if (isLast) R.color.red else R.color.black)
        )

        // Oculta el divisor en la última opción
        holder.divider.visibility = if (isLast) View.GONE else View.VISIBLE

        // Fondo personalizado según posición (primero, último o intermedio)
        val backgroundRes = when (position) {
            0 -> R.drawable.item_bg_first_menu_training
            items.lastIndex -> R.drawable.item_bg_last_menu_training
            else -> R.drawable.item_bg_middle_menu_training
        }
        holder.itemView.background = ContextCompat.getDrawable(context, backgroundRes)

        // Accesibilidad: descripción hablada para cada opción
        holder.text.contentDescription = "Training menu option ${items[position]}"
    }

    override fun getItemCount() = items.size
}
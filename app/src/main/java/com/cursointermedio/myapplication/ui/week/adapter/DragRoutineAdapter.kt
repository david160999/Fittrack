package com.cursointermedio.myapplication.ui.week.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.domain.model.RoutineModel
import java.util.Collections

// Adaptador para arrastrar y reordenar rutinas en un RecyclerView.
// Expone métodos para mover elementos y actualizar la lista de rutinas.

class DragRoutineAdapter(
    private val routines: MutableList<RoutineModel> = mutableListOf()
) : RecyclerView.Adapter<DragRoutineAdapter.RoutineViewHolder>() {

    // ViewHolder para cada rutina
    inner class RoutineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tvNameDragAdapter)
    }

    // Infla el layout del ítem de rutina
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drag_routine, parent, false)
        return RoutineViewHolder(view)
    }

    // Asigna el nombre de la rutina al TextView
    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        holder.textView.text = routines[position].name
    }

    // Devuelve el tamaño de la lista de rutinas
    override fun getItemCount(): Int = routines.size

    // Mueve un ítem de una posición a otra y notifica el cambio
    fun moveItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(routines, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    // Actualiza la lista de rutinas y refresca la vista
    fun updateList(newList: List<RoutineModel>) {
        routines.clear()
        routines.addAll(newList)
        notifyDataSetChanged()
    }
}
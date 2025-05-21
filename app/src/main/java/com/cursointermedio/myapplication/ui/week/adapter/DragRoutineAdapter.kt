package com.cursointermedio.myapplication.ui.week.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.google.android.material.divider.MaterialDivider
import java.util.Collections

class DragRoutineAdapter(
    private val routines: MutableList<RoutineModel> = mutableListOf<RoutineModel>()

) : RecyclerView.Adapter<DragRoutineAdapter.RoutineViewHolder>() {

    inner class RoutineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tvNameDragAdapter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_drag_routine, parent, false)
        return RoutineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        holder.textView.text = routines[position].name

    }

    override fun getItemCount(): Int = routines.size

    fun moveItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(routines, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun updateList(newList: List<RoutineModel>) {
        routines.clear()
        routines.addAll(newList)
        notifyDataSetChanged()
    }

}
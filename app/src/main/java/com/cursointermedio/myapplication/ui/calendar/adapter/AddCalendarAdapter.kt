package com.cursointermedio.myapplication.ui.calendar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.google.android.material.divider.MaterialDivider

class AddCalendarAdapter(
    private val items: List<String>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<AddCalendarAdapter.ViewHolder>() {

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val isLast = position == items.lastIndex

        // Set text
        holder.text.text = items[position]

        // Set visibility of divider
        holder.divider.visibility = if (isLast) View.GONE else View.VISIBLE

        // Set background
        val backgroundRes = when (position) {
            0 -> R.drawable.item_bg_first_menu_training
            items.lastIndex -> R.drawable.item_bg_last_menu_training
            else -> R.drawable.item_bg_middle_menu_training
        }
        holder.itemView.background = ContextCompat.getDrawable(context, backgroundRes)

        // Accessibility
        holder.text.contentDescription = "Training menu option ${items[position]}"
    }

    override fun getItemCount() = items.size
}
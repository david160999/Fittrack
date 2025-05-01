package com.cursointermedio.myapplication.ui.training.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.google.android.material.divider.MaterialDivider

class TrainingMenuAdapter(
    private val items: List<String>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<TrainingMenuAdapter.ViewHolder>() {

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.tvNameMenuTraining)
        val divider: MaterialDivider = view.findViewById(R.id.diverMenuTraining)

        init {
            view.setOnClickListener {
                onClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_menu_training, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = items[position]

        val context = holder.itemView.context
        val isLast = position == items.lastIndex

        if (isLast) {
            holder.text.setTextColor(ContextCompat.getColor(context, R.color.red))
            holder.divider.visibility = MaterialDivider.GONE
        }

        val backgroundRes = when (position) {
            0 -> R.drawable.item_bg_first_menu_training
            items.lastIndex -> R.drawable.item_bg_last_menu_training
            else -> R.drawable.item_bg_middle_menu_training
        }
        holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, backgroundRes)

    }

    override fun getItemCount() = items.size
}
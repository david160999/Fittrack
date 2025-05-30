package com.cursointermedio.myapplication.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.google.android.material.divider.MaterialDivider

class HomeMenuOptionAdapter(
    private val items: List<String>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<HomeMenuOptionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.tvHomeMenuOption)

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.text.text = items[position]

        val backgroundRes = R.drawable.item_bg_menu_home
        holder.itemView.background = ContextCompat.getDrawable(context, backgroundRes)

    }

    override fun getItemCount() = items.size
}
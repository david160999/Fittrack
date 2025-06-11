package com.cursointermedio.myapplication.ui.week.dialog.calendar.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.cursointermedio.myapplication.R

// Adaptador para un calendario simple usando BaseAdapter (para GridView o ListView).
// Permite seleccionar y deseleccionar días, y aplicar estilos visuales a los seleccionados.

class CalendarAdapter(
    private val context: Context,
    private val days: List<String>,
    private var selectedPositionList: MutableList<Int> = mutableListOf()
) : BaseAdapter() {

    override fun getCount(): Int = days.size

    override fun getItem(position: Int): Any = days[position]

    override fun getItemId(position: Int): Long = position.toLong()

    // Selecciona un día (por posición)
    fun setSelected(selected: Int) {
        if (!selectedPositionList.contains(selected)) {
            selectedPositionList.add(selected)
            notifyDataSetChanged()
        }
    }

    // Deselecciona un día según el número real del día (1...31)
    fun removeSelected(realDay: Int) {
        val indexInDays = days.indexOfFirst { it.toIntOrNull() != null }
        val selectedPosition = indexInDays + (realDay - 1)
        selectedPositionList.remove(selectedPosition)
        notifyDataSetChanged()
    }

    // Selecciona varios días iniciales a partir de una lista de días reales (1...31)
    fun setFirstSelected(realDayList: List<Int>) {
        val indexInDays = days.indexOfFirst { it.toIntOrNull() != null }
        realDayList.forEach { day ->
            val selectedPosition = indexInDays + (day - 1)
            if (!selectedPositionList.contains(selectedPosition)) {
                selectedPositionList.add(selectedPosition)
            }
        }
        notifyDataSetChanged()
    }

    // Devuelve la vista para cada celda del calendario
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_calendar_day, parent, false)
        val dayText = view.findViewById<TextView>(R.id.dayText)
        dayText.text = days[position]

        // Marca visual para días seleccionados
        if (selectedPositionList.contains(position) && days[position].isNotEmpty()) {
            dayText.setBackgroundResource(R.drawable.selected_day_background)
            dayText.setTextColor(Color.WHITE)
        } else {
            dayText.setBackgroundColor(Color.TRANSPARENT)
            dayText.setTextColor(Color.BLACK)
        }

        return view
    }
}
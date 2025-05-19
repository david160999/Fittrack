package com.cursointermedio.myapplication.ui.week.dialog.calendar.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.cursointermedio.myapplication.R

class CalendarAdapter(
    private val context: Context,
    private val days: List<String>,
    private var selectedPositionList: MutableList<Int> = mutableListOf()

) : BaseAdapter() {

    override fun getCount(): Int = days.size

    override fun getItem(position: Int): Any = days[position]

    override fun getItemId(position: Int): Long = position.toLong()

    fun setSelected(selected : Int) {
        selectedPositionList.add(selected)
        notifyDataSetChanged()
    }
    fun removeSelected(realDay : Int){
        val indexInDays = days.indexOfFirst { it.toIntOrNull() != null }
        val selectedPosition = indexInDays + (realDay - 1)

        selectedPositionList.remove(selectedPosition)
        notifyDataSetChanged()
    }

    fun setFirstSelected(realDayList: List<Int>) {
        val indexInDays = days.indexOfFirst { it.toIntOrNull() != null }
        realDayList.map { day ->
            val selectedPosition = indexInDays + (day - 1)
            selectedPositionList.add(selectedPosition)
            notifyDataSetChanged()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_calendar_day, parent, false)
        val dayText = view.findViewById<TextView>(R.id.dayText)
        dayText.text = days[position]


        // Marca visual
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
package com.cursointermedio.myapplication.ui.calendar.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.DateWithTrac
import com.cursointermedio.myapplication.databinding.ItemMainCalendarDayBinding

class MainCalendarAdapter(
    private val context: Context,
    private val days: List<String>,
    private var dateEntityList: List<DateWithTrac?>,
    private var selectedPosition: Int = -1,
    private val fullDayList: List<String>

) : BaseAdapter() {

    override fun getCount(): Int = days.size

    override fun getItem(position: Int): Any = days[position]

    override fun getItemId(position: Int): Long = position.toLong()

    fun setSelected(selected: Int) {
        selectedPosition = selected
        notifyDataSetChanged()
    }

    fun setFirstSelected(realDay: Int) {
        val indexInDays = days.indexOfFirst { it.toIntOrNull() != null }
        selectedPosition = indexInDays + (realDay - 1)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ItemMainCalendarDayBinding
        val view: View

        if (convertView == null) {
            binding =
                ItemMainCalendarDayBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemMainCalendarDayBinding
        }

        val day = days[position]
        binding.tvMainCalendarDay.text = day

        // Estilo para el día seleccionado
        if (selectedPosition == position && day.isNotEmpty()) {
            binding.root.setBackgroundResource(R.drawable.selected_day_background_main_calendar)
            binding.tvMainCalendarDay.setTextColor(Color.WHITE)
        } else {
            binding.root.setBackgroundColor(Color.TRANSPARENT)
            binding.tvMainCalendarDay.setTextColor(Color.BLACK)
        }

        // Ocultar indicadores por defecto
        binding.viewWeight.visibility = View.INVISIBLE
        binding.viewNotes.visibility = View.INVISIBLE
        binding.viewTrac.visibility = View.INVISIBLE

        // Mostrar indicadores si hay datos
        for (dateEntity in dateEntityList) {
            if (dateEntity == null) continue

            if (fullDayList[position].contains(dateEntity.dateEntity.dateId)) {
                if (dateEntity.dateEntity.bodyWeight != null) {
                    binding.viewWeight.visibility = View.VISIBLE
                }

                if (dateEntity.dateEntity.note != null) {
                    binding.viewNotes.visibility = View.VISIBLE
                }
                if (dateEntity.tracEntity != null) {
                    binding.viewTrac.visibility = View.VISIBLE
                }

                break // Termina el loop al encontrar el item correspondiente
            }
        }

        return view
    }

    fun updateDateEntityList(newList: List<DateWithTrac?>) {
        this.dateEntityList = newList
        notifyDataSetChanged() // o DiffUtil si quieres hacerlo más eficiente
    }
}


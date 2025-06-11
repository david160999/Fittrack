package com.cursointermedio.myapplication.ui.week.dialog.calendar.adapter

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.ItemCalendarRoutineBinding
import com.cursointermedio.myapplication.domain.model.RoutineModel

// ViewHolder para cada rutina en el calendario, mostrando nombre, fecha y cambios visuales en selección.

class CalendarRoutineViewHolder(
    private val binding: ItemCalendarRoutineBinding
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("ClickableViewAccessibility")
    fun bind(
        routine: RoutineModel
    ) {
        binding.tvExerciseName.text = routine.name
        if (routine.date.isNullOrBlank()) {
            binding.tvDatesRoutine.text =
                ContextCompat.getString(binding.root.context, R.string.week_date_routine)
        } else {
            binding.tvDatesRoutine.text = routine.date
        }
    }

    // Estado visual por defecto (no seleccionado)
    fun defaultBg() {
        val context = binding.root.context
        binding.tvExerciseName.setTextColor(ContextCompat.getColor(context, R.color.black))
        binding.tvDatesRoutine.setTextColor(ContextCompat.getColor(context, R.color.Grey))
        binding.cvCalendarRoutine.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
        binding.ivCalendarRoutine.setColorFilter(ContextCompat.getColor(context, R.color.icons))
    }

    // Estado visual seleccionado
    fun selectedBg() {
        val context = binding.root.context
        binding.tvExerciseName.setTextColor(ContextCompat.getColor(context, R.color.white))
        binding.tvDatesRoutine.setTextColor(ContextCompat.getColor(context, R.color.white))
        binding.cvCalendarRoutine.setCardBackgroundColor(ContextCompat.getColor(context, R.color.icons))
        binding.ivCalendarRoutine.setColorFilter(ContextCompat.getColor(context, R.color.white))
    }
}
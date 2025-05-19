package com.cursointermedio.myapplication.ui.week.dialog.calendar

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.DialogAddCalendarBinding
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.useCase.CopyOption
import com.cursointermedio.myapplication.ui.week.dialog.calendar.adapter.CalendarAdapter
import com.cursointermedio.myapplication.ui.week.dialog.calendar.adapter.CalendarRoutineAdapter
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class AddCalendarDialog(
    private val onSaveClickListener: (List<RoutineModel>, List<LocalDate?>) -> Unit,
    private val routines: List<RoutineModel>
) : BottomSheetDialogFragment() {

    private var _binding: DialogAddCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var monthYearText: TextView
    private lateinit var calendarGrid: GridView
    private var selectedDate: LocalDate = LocalDate.now()

    private lateinit var prevMonth: ImageView
    private lateinit var nextMonth: ImageView

    private var fixSelectedDayList: MutableList<LocalDate?> = MutableList(routines.size) { null }
    private var lastDaySelectedList: MutableList<LocalDate?> = MutableList(routines.size) { null }
    private var removeSelectedDayList: MutableList<LocalDate?> = MutableList(routines.size) { null }

    private var selectedRoutine: Int? = 0
    private lateinit var adapterRoutines: CalendarRoutineAdapter

    private var localRoutines = routines.toList() // esta sí es mutable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogAddCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ahora puedes personalizar tu diálogo
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        initUi()
        setMonthView()
        initListeners()
    }

    private fun setMonthView() {
        val yearMonth = YearMonth.from(selectedDate)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstDayOfMonth = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

        val monthName = selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        monthYearText.text = "$monthName ${selectedDate.year}"

        val dayList = mutableListOf<String>()

        for (i in 1..dayOfWeek) {
            dayList.add("") // espacios vacíos antes del 1
        }

        for (day in 1..daysInMonth) {
            dayList.add(day.toString())
        }

        val adapter = CalendarAdapter(binding.root.context, dayList)
        calendarGrid.adapter = adapter

        val listDays = mutableListOf<Int>()
        fixSelectedDayList.map { fixDay ->
            if (fixDay != null) {
                if (fixDay.month == selectedDate.month && fixDay.year == selectedDate.year) {
                    listDays.add(fixDay.dayOfMonth)
                }
            }
        }
        if (listDays.isNotEmpty()) {
            adapter.setFirstSelected(listDays)
        }


        calendarGrid.setOnItemClickListener { _, _, position, _ ->
            val day = dayList[position]
            if (day.isNotEmpty()) {
                val indexInDays = dayList.indexOfFirst { it.toIntOrNull() != null }
                val realDay = position - (indexInDays - 1)
                val realDate = LocalDate.of(selectedDate.year, selectedDate.month, realDay)

                if (!lastDaySelectedList.contains(realDate) && selectedRoutine != null) {
                    if (lastDaySelectedList[selectedRoutine!!] != null) {
                        val lastDay = lastDaySelectedList[selectedRoutine!!]?.dayOfMonth!!
                        adapter.removeSelected(lastDay)
                    }

                    fixSelectedDayList[selectedRoutine!!] = realDate
                    setDateToRoutine(selectedRoutine!!)

                    adapter.setSelected(position)
                    lastDaySelectedList[selectedRoutine!!] = fixSelectedDayList[selectedRoutine!!]
                } else if (lastDaySelectedList.contains(realDate) && selectedRoutine == null) {
                    val index = lastDaySelectedList.indexOf(realDate)

                    removeSelectedDayList[index] = fixSelectedDayList[index]
                    fixSelectedDayList[index] = null
                    lastDaySelectedList[index] = null

                    adapter.removeSelected(realDay)
                    setDateToRoutine(index)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi() {
        val listDays = listOf(
            ContextCompat.getString(binding.root.context, R.string.weekDay_Mon),
            ContextCompat.getString(binding.root.context, R.string.weekDay_Tue),
            ContextCompat.getString(binding.root.context, R.string.weekDay_Wed),
            ContextCompat.getString(binding.root.context, R.string.weekDay_Thu),
            ContextCompat.getString(binding.root.context, R.string.weekDay_Fri),
            ContextCompat.getString(binding.root.context, R.string.weekDay_Sat),
            ContextCompat.getString(binding.root.context, R.string.weekDay_Sun)
        )

        val adapterDays =
            ArrayAdapter(binding.root.context, R.layout.item_calendar_week_day, listDays)
        binding.daysGrid.adapter = adapterDays

        adapterRoutines = CalendarRoutineAdapter(onItemSelected = { position ->
            getSelectedRoutine(position)
        })
        val layoutManager = LinearLayoutManager(context)
        binding.rvAddCalendarRoutines.layoutManager = layoutManager
        binding.rvAddCalendarRoutines.adapter = adapterRoutines


        monthYearText = binding.monthYearText
        calendarGrid = binding.calendarGrid
        prevMonth = binding.prevMonth
        nextMonth = binding.nextMonth


        fixSelectedDayList[selectedRoutine!!] = selectedDate
        lastDaySelectedList[selectedRoutine!!] = selectedDate

        routines.forEachIndexed { index, routine ->
            if (!routine.date.isNullOrBlank()) {
                val dateParsed = LocalDate.parse(routine.date.toString())
                fixSelectedDayList[index] = dateParsed
                lastDaySelectedList[index] = dateParsed
            }
        }
        setDateToRoutine(selectedRoutine!!)
    }

    private fun getSelectedRoutine(position: Int?) {
        selectedRoutine = position
    }

    private fun setDateToRoutine(indexSelected: Int) {
        localRoutines = localRoutines.mapIndexed { index, routine ->
            if (index == indexSelected) {
                val newDate = fixSelectedDayList.getOrNull(indexSelected)?.toString()
                routine.copy(date = newDate)
            } else {
                routine
            }
        }
        adapterRoutines.submitList(localRoutines)

    }

    private fun initListeners() {
        prevMonth.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            setMonthView()
        }

        nextMonth.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            setMonthView()
        }
        binding.btnCalendarCancel.setupTouchAction {
            dialog?.dismiss()
        }
        binding.btnCalendarSave.setupTouchAction {
            onSaveClickListener(localRoutines, removeSelectedDayList)
            dialog?.dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogAnimationStyle
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            it.background = null
            // Cambiar altura máxima a, por ejemplo, el 80% de la pantalla
            val layoutParams = it.layoutParams
            val displayMetrics = Resources.getSystem().displayMetrics
            val maxHeight = (displayMetrics.heightPixels * 0.95).toInt()

            layoutParams.height = maxHeight
            it.layoutParams = layoutParams
            behavior.isDraggable = true

            behavior.peekHeight = maxHeight  // para que empiece con esa altura

            behavior.state = BottomSheetBehavior.STATE_EXPANDED // para abrir expandido si querés
        }
    }
}
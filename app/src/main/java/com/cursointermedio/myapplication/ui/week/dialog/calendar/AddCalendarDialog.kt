package com.cursointermedio.myapplication.ui.week.dialog.calendar

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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

// Diálogo BottomSheet para asignar fechas de calendario a rutinas.
// Permite seleccionar un día del calendario para cada rutina, cambiar de mes y guardar la configuración.

class AddCalendarDialog(
    private val onSaveClickListener: (List<RoutineModel>, List<LocalDate?>) -> Unit, // Callback con rutinas y días eliminados
    private val routines: List<RoutineModel>
) : BottomSheetDialogFragment() {

    private var _binding: DialogAddCalendarBinding? = null
    private val binding get() = _binding!!

    // Referencias a vistas del calendario y controles de mes
    private lateinit var monthYearText: TextView
    private lateinit var calendarGrid: GridView
    private var selectedDate: LocalDate = LocalDate.now() // Fecha actualmente mostrada

    private lateinit var prevMonth: ImageView
    private lateinit var nextMonth: ImageView

    // Listas auxiliares para manejar selección de días por rutina
    private var fixSelectedDayList: MutableList<LocalDate?> = MutableList(routines.size) { null }
    private var lastDaySelectedList: MutableList<LocalDate?> = MutableList(routines.size) { null }
    private var removeSelectedDayList: MutableList<LocalDate?> = MutableList(routines.size) { null }

    private var selectedRoutine: Int? = 0 // Índice de la rutina seleccionada actualmente
    private lateinit var adapterRoutines: CalendarRoutineAdapter

    private var localRoutines = routines.toList() // Copia local mutable de las rutinas

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

        // Personaliza el fondo y el modo de teclado del diálogo
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        initUi()         // Inicializa UI: días de la semana, rutinas y estado inicial
        setMonthView()   // Dibuja el calendario del mes actual
        initListeners()  // Listeners para botones y calendario
    }

    // Dibuja el calendario del mes mostrado y marca los días seleccionados de cada rutina
    private fun setMonthView() {
        val yearMonth = YearMonth.from(selectedDate)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstDayOfMonth = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

        // Nombre del mes y año en el encabezado
        val monthName = selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        monthYearText.text = "$monthName ${selectedDate.year}"

        val dayList = mutableListOf<String>()
        // Agrega celdas vacías para alinear el primer día de la semana
        for (i in 1..dayOfWeek) {
            dayList.add("")
        }
        // Agrega los días del mes
        for (day in 1..daysInMonth) {
            dayList.add(day.toString())
        }

        // Adapter del calendario
        val adapter = CalendarAdapter(binding.root.context, dayList)
        calendarGrid.adapter = adapter

        // Marca los días seleccionados para el mes actual
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

        // Listener para selección de días
        calendarGrid.setOnItemClickListener { _, _, position, _ ->
            val day = dayList[position]
            if (day.isNotEmpty()) {
                val indexInDays = dayList.indexOfFirst { it.toIntOrNull() != null }
                val realDay = position - (indexInDays - 1)
                val realDate = LocalDate.of(selectedDate.year, selectedDate.month, realDay)

                // Si el día aún no está seleccionado para la rutina
                if (!lastDaySelectedList.contains(realDate) && selectedRoutine != null) {
                    // Si ya había un día seleccionado, lo desmarca
                    if (lastDaySelectedList[selectedRoutine!!] != null) {
                        val lastDay = lastDaySelectedList[selectedRoutine!!]?.dayOfMonth!!
                        adapter.removeSelected(lastDay)
                    }
                    // Marca el nuevo día para la rutina seleccionada
                    fixSelectedDayList[selectedRoutine!!] = realDate
                    setDateToRoutine(selectedRoutine!!)
                    adapter.setSelected(position)
                    lastDaySelectedList[selectedRoutine!!] = fixSelectedDayList[selectedRoutine!!]
                }
                // Si el día está seleccionado y ninguna rutina está activa, lo elimina de la lista
                else if (lastDaySelectedList.contains(realDate) && selectedRoutine == null) {
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

    // Inicializa la UI: días de la semana, lista de rutinas y estado inicial de selección
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
        // Adapter de los días de la semana (encabezado)
        val adapterDays =
            ArrayAdapter(binding.root.context, R.layout.item_calendar_week_day, listDays)
        binding.daysGrid.adapter = adapterDays

        // Adapter y layout de rutinas para seleccionar
        adapterRoutines = CalendarRoutineAdapter(onItemSelected = { position ->
            getSelectedRoutine(position)
        })
        val layoutManager = LinearLayoutManager(context)
        binding.rvAddCalendarRoutines.layoutManager = layoutManager
        binding.rvAddCalendarRoutines.adapter = adapterRoutines

        // Referencias de vistas de calendario
        monthYearText = binding.monthYearText
        calendarGrid = binding.calendarGrid
        prevMonth = binding.prevMonth
        nextMonth = binding.nextMonth

        // Inicializa el primer día seleccionado con la fecha actual
        fixSelectedDayList[selectedRoutine!!] = selectedDate
        lastDaySelectedList[selectedRoutine!!] = selectedDate

        // Si las rutinas ya tenían fecha, las carga en las listas auxiliares
        routines.forEachIndexed { index, routine ->
            if (!routine.date.isNullOrBlank()) {
                val dateParsed = LocalDate.parse(routine.date.toString())
                fixSelectedDayList[index] = dateParsed
                lastDaySelectedList[index] = dateParsed
            }
        }
        setDateToRoutine(selectedRoutine!!)
    }

    // Cambia la rutina seleccionada cuando el usuario toca una rutina distinta
    private fun getSelectedRoutine(position: Int?) {
        selectedRoutine = position
    }

    // Actualiza la fecha de la rutina seleccionada y refresca el RecyclerView de rutinas
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

    // Listeners para los botones de navegación de mes y para los botones de cancelar/guardar
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
            // Devuelve la lista de rutinas y los días eliminados al callback
            onSaveClickListener(localRoutines, removeSelectedDayList)
            dialog?.dismiss()
        }
    }

    // Establece el estilo del BottomSheet (animación y altura)
    override fun getTheme(): Int {
        return R.style.BottomSheetDialogAnimationStyle
    }

    // Ajusta la altura máxima y estado expandido del BottomSheet al iniciar
    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            it.background = null
            val layoutParams = it.layoutParams
            val displayMetrics = Resources.getSystem().displayMetrics
            val maxHeight = (displayMetrics.heightPixels * 0.95).toInt() // 95% de la pantalla

            layoutParams.height = maxHeight
            it.layoutParams = layoutParams
            behavior.isDraggable = true
            behavior.peekHeight = maxHeight
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}
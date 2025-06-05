package com.cursointermedio.myapplication.ui.calendar

import android.animation.LayoutTransition
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.FragmentCalendarBinding
import com.cursointermedio.myapplication.ui.calendar.adapter.AddCalendarAdapter
import com.cursointermedio.myapplication.ui.calendar.adapter.MainCalendarAdapter
import com.cursointermedio.myapplication.ui.home.dialog.AddNoteDialog
import com.cursointermedio.myapplication.ui.home.dialog.AddWeightDialog
import com.cursointermedio.myapplication.ui.home.dialog.TracDialog
import com.cursointermedio.myapplication.ui.training.adapter.TrainingMenuAdapter
import com.cursointermedio.myapplication.utils.extensions.createMenuOption
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment @Inject constructor() : Fragment() {
    private val calendarViewModel by viewModels<CalendarViewModel>()

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var monthYearText: TextView
    private lateinit var calendarGrid: GridView
    private var selectedDate: LocalDate = LocalDate.now()
    private var fixSelectedDate = selectedDate

    private lateinit var adapter: MainCalendarAdapter

    private lateinit var prevMonth: ImageView
    private lateinit var nextMonth: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initListeners()
    }

    private fun initUi() {

        binding.lyMainCalendarViews.layoutTransition = LayoutTransition().apply {
            enableTransitionType(LayoutTransition.CHANGING)
        }

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

        monthYearText = binding.monthYearText
        calendarGrid = binding.calendarGrid
        prevMonth = binding.prevMonth
        nextMonth = binding.nextMonth

        setMonthView()
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
        binding.btnMainCalendarSave.setupTouchAction {
            createMenuAddCalendar()
        }

        binding.cvMainCalendarNotes.setupTouchAction {
            createAddNoteDialog()
        }
        binding.cvMainCalendarWeight.setupTouchAction {
            createAddWeightDialog()

        }
        binding.cvMainCalendarTrac.setupTouchAction {
            createTracDialog()
        }
        binding.btnDeleteMainCalendarNotes.setupTouchAction {
            createMenuOption(
                requireContext(),
                binding.btnDeleteMainCalendarNotes,
                ContextCompat.getString(requireContext(), R.string.home_notes_delete)
            ) { calendarViewModel.deleteNote() }
        }
        binding.btnDeleteMainCalendarWeight.setupTouchAction {
            createMenuOption(
                requireContext(),
                binding.btnDeleteMainCalendarWeight,
                ContextCompat.getString(requireContext(), R.string.home_weight_delete)
            ) { calendarViewModel.deleteBodyWeight() }
        }
        binding.btnDeleteMainCalendarTrac.setupTouchAction {
            createMenuOption(
                requireContext(),
                binding.btnDeleteMainCalendarTrac,
                ContextCompat.getString(requireContext(), R.string.home_trac_delete)
            ) { calendarViewModel.deleteTrac() }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    calendarViewModel.dateList.collectLatest {
                        adapter.updateDateEntityList(it)
                    }
                }
                launch {
                    calendarViewModel.userWeight.collectLatest {weight->
                        if (weight != null) {
                            if (weight.isNotBlank()) {
                                binding.tvMainCalendarResultWeight.text = weight.toString()
                                binding.cvMainCalendarWeight.visibility = View.VISIBLE
                            } else {
                                binding.cvMainCalendarWeight.visibility = View.GONE
                            }
                        }
                    }
                }
                launch {
                    calendarViewModel.dateSelected.collectLatest {
                        it?.let {
                            if (it.dateEntity.note != null) {
                                binding.tvMainCalendarResultNotes.text =
                                    it.dateEntity.note.toString()
                                binding.cvMainCalendarNotes.visibility = View.VISIBLE
                            } else {
                                binding.cvMainCalendarNotes.visibility = View.GONE
                            }

                            if (it.dateEntity.bodyWeight != null) {
                                binding.tvMainCalendarResultWeight.text =
                                    it.dateEntity.bodyWeight.toString()
                                binding.cvMainCalendarWeight.visibility = View.VISIBLE
                            } else {
                                binding.cvMainCalendarWeight.visibility = View.GONE
                            }

                            if (it.tracEntity != null) {
                                val push = it.tracEntity.push ?: 0
                                val pull = it.tracEntity.pull ?: 0
                                val leg = it.tracEntity.leg ?: 0
                                val mental = (it.tracEntity.motivation ?: 0) +
                                        (it.tracEntity.recuperation ?: 0) +
                                        (it.tracEntity.rest ?: 0) +
                                        (it.tracEntity.technique ?: 0)

                                binding.tvResultPush.text =
                                    getString(R.string.home_sub_trac_push, push)
                                binding.tvResultPull.text =
                                    getString(R.string.home_sub_trac_pull, pull)
                                binding.tvResultLeg.text =
                                    getString(R.string.home_sub_trac_leg, leg)
                                binding.tvResultMental.text =
                                    getString(R.string.home_sub_trac_mental, mental)

                                binding.cvMainCalendarTrac.visibility = View.VISIBLE
                            } else {
                                binding.cvMainCalendarTrac.visibility = View.GONE
                            }

                        } ?: run {
                            // Si 'it' es null, ocultamos todas las vistas
                            binding.cvMainCalendarNotes.visibility = View.GONE
                            binding.cvMainCalendarWeight.visibility = View.GONE
                            binding.cvMainCalendarTrac.visibility = View.GONE
                        }
                    }

                }
            }
        }
    }

    private fun createMenuAddCalendar() {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu_training, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val recyclerView = popupView.findViewById<RecyclerView>((R.id.rvTrainingMenu))
        recyclerView.layoutManager = LinearLayoutManager(context)

        val items = listOf(
            ContextCompat.getString(requireContext(), R.string.calendar_menuOption1),
            ContextCompat.getString(requireContext(), R.string.calendar_menuOption2),
            ContextCompat.getString(requireContext(), R.string.calendar_menuOption3),
        )

        val adapter = AddCalendarAdapter(items) { position ->
            when (position) {
                0 -> {createAddNoteDialog()}
                1 -> {createAddWeightDialog()}
                2 -> {createTracDialog()}
            }
            popupWindow.dismiss()
        }

        recyclerView.adapter = adapter

        popupWindow.apply {
            isFocusable = true
            isOutsideTouchable = true
            elevation = 12f
            isClippingEnabled = true
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            animationStyle = R.style.MenuTRainingPopupFadeAnimation
        }

        popupWindow.showAsDropDown(binding.btnMainCalendarSave, 0, 0)


    }

    private fun setMonthView() {
        val yearMonth = YearMonth.from(selectedDate)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstDayOfMonth = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

        val monthName = selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        monthYearText.text = "$monthName ${selectedDate.year}"

        val dayList = mutableListOf<String>()
        val fullDayList = mutableListOf<String>()

        // Agrega espacios vacíos al principio (no son fechas reales)
        for (i in 1..dayOfWeek) {
            dayList.add("") // espacios vacíos antes del 1
            fullDayList.add("")
        }

        // Agrega los días reales con formato
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        for (day in 1..daysInMonth) {
            dayList.add(day.toString())
            val date = selectedDate.withDayOfMonth(day)
            fullDayList.add(date.format(formatter))
        }

        adapter = MainCalendarAdapter(
            context = binding.root.context,
            days = dayList,
            dateEntityList = emptyList(),
            fullDayList = fullDayList
        )
        calendarGrid.adapter = adapter

        if (fixSelectedDate.month == selectedDate.month && fixSelectedDate.year == selectedDate.year){
            adapter.setFirstSelected(fixSelectedDate.dayOfMonth)
        }

        calendarGrid.setOnItemClickListener { _, _, position, _ ->
            val day = dayList[position]
            if (day.isNotEmpty()) {
                val indexInDays = dayList.indexOfFirst { it.toIntOrNull() != null }
                val realDay = position - (indexInDays - 1)
                val realDate = LocalDate.of(selectedDate.year, selectedDate.month, realDay)
                fixSelectedDate = realDate

                adapter.setSelected(position)
                calendarViewModel.getSelectedDateWithTracFlow(fullDayList[position])
            }
        }

        calendarViewModel.getDateListFlow(fullDayList)
    }

    private fun createAddNoteDialog() {
        val view = binding.cvMainCalendarNotes
        hideViewWithAnimation(view)
        val dialog = AddNoteDialog(
            onDismissCallback = { showViewWithAnimation(view) },
            onSaveClick = { notes -> calendarViewModel.updateNote(notes, fixSelectedDate.toString()) },
            notes = calendarViewModel.dateSelected.value?.dateEntity?.note
        )
        dialog.show(parentFragmentManager, "dialog")

    }

    private fun createAddWeightDialog() {
        val view = binding.cvMainCalendarWeight
        hideViewWithAnimation(view)
        val dialog = AddWeightDialog(
            onDismissCallback = { showViewWithAnimation(view) },
            onSaveClick = { weight -> calendarViewModel.updateBodyWeight(weight, fixSelectedDate.toString()) },
            weight = calendarViewModel.dateSelected.value?.dateEntity?.bodyWeight
        )
        dialog.show(parentFragmentManager, "dialog")

    }

    private fun createTracDialog() {
        val dialog = TracDialog(
            tracEntity = calendarViewModel.dateSelected.value?.tracEntity,
            onItemSave = { trac ->
                calendarViewModel.insertOrUpdateTrac(trac, fixSelectedDate.toString())
            })
        dialog.show(parentFragmentManager, "dialog")
    }

    private fun hideViewWithAnimation(view: View) {
        val layout = binding.lyMainCalendarViews
        val transition = layout.layoutTransition
        layout.layoutTransition = null  // Desactiva momentáneamente

        view.animate()
            .alpha(0f)
            .translationY(view.height.toFloat() / 2) // baja media altura, ajustá como quieras
            .setDuration(300)
            .withEndAction {
                view.visibility = View.GONE
                view.translationY = 0f  // reset para la próxima vez que aparezca
                view.alpha = 1f         // reset alpha también
                layout.layoutTransition = transition // Vuelve a activar después
            }
            .start()
    }

    private fun showViewWithAnimation(view: View) {
        view.apply {
            alpha = 0f
            translationY = view.height.toFloat() / 2
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .start()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val currentDay = LocalDate.now()
        calendarViewModel.getSelectedDateWithTracFlow(currentDay.toString())
    }
}
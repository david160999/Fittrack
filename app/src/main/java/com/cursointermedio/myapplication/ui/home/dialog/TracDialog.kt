package com.cursointermedio.myapplication.ui.home.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.TracEntity
import com.cursointermedio.myapplication.databinding.DialogExerciseDescriptionBinding
import com.cursointermedio.myapplication.databinding.DialogTracBinding
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.getExerciseDescriptionFromKey
import com.cursointermedio.myapplication.domain.model.getExerciseNameFromKey
import com.cursointermedio.myapplication.ui.training.adapter.TrainingMenuAdapter
import com.cursointermedio.myapplication.utils.extensions.isItemBelowThreshold
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate

class TracDialog(
    private val tracEntity: TracEntity?,
    private val onItemSave: (TracEntity) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogTracBinding? = null
    private val binding get() = _binding!!

    private lateinit var legList: List<String>
    private lateinit var pushList: List<String>
    private lateinit var pullList: List<String>
    private lateinit var foodList: List<String>
    private lateinit var restList: List<String>
    private lateinit var motivationList: List<String>
    private lateinit var technicList: List<String>

    private var isSelecting: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogTracBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        initUi()
        initListeners()

    }

    private fun initListeners() {
        binding.flLeg.setupTouchAction {
            createOptionMenu(legList, binding.resultLeg, binding.flLeg)

        }
        binding.flPush.setupTouchAction {
            createOptionMenu(pushList, binding.resultPush, binding.flPush)

        }
        binding.flPull.setupTouchAction {
            createOptionMenu(pullList, binding.resultPull, binding.flPull)
        }
        binding.flfood.setupTouchAction {
            createOptionMenu(foodList, binding.resultFood, binding.flfood)
        }
        binding.flRest.setupTouchAction {
            createOptionMenu(restList, binding.resultRest, binding.flRest)
        }
        binding.flMotivation.setupTouchAction {
            createOptionMenu(motivationList, binding.resultMotivation, binding.flMotivation)
        }
        binding.flTechnic.setupTouchAction {
            createOptionMenu(technicList, binding.resultTechnic, binding.flTechnic)
        }
        binding.btnAddTrac.setupTouchAction {
            val newTrac = tracEntity?.copy(
                leg = binding.resultLeg.text.toString().toInt(),
                push = binding.resultPush.text.toString().toInt(),
                pull = binding.resultPull.text.toString().toInt(),
                rest = binding.resultFood.text.toString().toInt(),
                recuperation = binding.resultRest.text.toString().toInt(),
                motivation = binding.resultMotivation.text.toString().toInt(),
                technique = binding.resultTechnic.text.toString().toInt()
            ) ?: TracEntity(
                tracId = 0, // se auto-genera
                dateId = LocalDate.now().toString(),
                leg = binding.resultLeg.text.toString().toInt(),
                push = binding.resultPush.text.toString().toInt(),
                pull = binding.resultPull.text.toString().toInt(),
                rest = binding.resultFood.text.toString().toInt(),
                recuperation = binding.resultRest.text.toString().toInt(),
                motivation = binding.resultMotivation.text.toString().toInt(),
                technique = binding.resultTechnic.text.toString().toInt()
            )
            onItemSave(newTrac)
            dialog?.dismiss()
        }
    }

    private fun createOptionMenu(items: List<String>, result: TextView, view: View) {
        view.alpha = 0.4f

        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu_training, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val recyclerView = popupView.findViewById<RecyclerView>((R.id.rvTrainingMenu))
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = TracMenuAdapter(items) { position ->
            when (position) {
                0 -> {}
                1 -> {
                    result.text = "1"
                }

                2 -> {
                    result.text = "2"
                }

                3 -> {
                    result.text = "3"
                }

                4 -> {
                    result.text = "4"
                }

                5 -> {
                    result.text = "5"
                }
            }
            popupWindow.dismiss()
        }

        recyclerView.adapter = adapter

        popupWindow.apply {
            isFocusable = true
            isOutsideTouchable = true
            elevation = 16f
            isClippingEnabled = true
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            animationStyle = R.style.MenuTRainingPopupFadeAnimation
            setOnDismissListener {
                view.alpha = 1f
            }
        }

        if (isItemBelowThreshold(view)) {
            popupWindow.showAsDropDown(view, 0, -50)
        } else {
            popupWindow.showAsDropDown(view, 450, 0)
        }
    }

    private fun initUi() {
        legList = getTrackedOptions("home_trac_leg")
        pushList = getTrackedOptions("home_trac_push")
        pullList = getTrackedOptions("home_trac_pull")
        foodList = getTrackedOptions("home_trac_food")
        restList = getTrackedOptions("home_trac_rest")
        motivationList = getTrackedOptions("home_trac_motivation")
        technicList = getTrackedOptions("home_trac_technic")

        binding.tvLeg.text = ContextCompat.getString(requireContext(), R.string.home_trac_leg)
        binding.tvPush.text = ContextCompat.getString(requireContext(), R.string.home_trac_push)
        binding.tvPull.text = ContextCompat.getString(requireContext(), R.string.home_trac_pull)
        binding.tvFood.text = ContextCompat.getString(requireContext(), R.string.home_trac_food)
        binding.tvRest.text = ContextCompat.getString(requireContext(), R.string.home_trac_rest)
        binding.tvMotivation.text =
            ContextCompat.getString(requireContext(), R.string.home_trac_motivation)
        binding.tvTechnic.text =
            ContextCompat.getString(requireContext(), R.string.home_trac_technic)

        if (tracEntity != null) {
            binding.resultLeg.text = tracEntity.leg?.toString() ?: "0"
            binding.resultPush.text = tracEntity.push?.toString() ?: "0"
            binding.resultPull.text = tracEntity.pull?.toString() ?: "0"
            binding.resultFood.text = tracEntity.rest?.toString() ?: "0"
            binding.resultRest.text = tracEntity.recuperation?.toString() ?: "0"
            binding.resultMotivation.text = tracEntity.motivation?.toString() ?: "0"
            binding.resultTechnic.text = tracEntity.technique?.toString() ?: "0"

        } else {
            binding.resultLeg.text = "0"
            binding.resultPush.text = "0"
            binding.resultPull.text = "0"
            binding.resultFood.text = "0"
            binding.resultRest.text = "0"
            binding.resultMotivation.text = "0"
            binding.resultTechnic.text = "0"
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun getTrackedOptions(prefix: String): List<String> {
        val defaultItem = ContextCompat.getString(requireContext(), R.string.home_trac_selection)
        val options = (1..5).map { index ->
            val resId = resources.getIdentifier(
                "${prefix}_option$index",
                "string",
                requireContext().packageName
            )
            ContextCompat.getString(requireContext(), resId)
        }
        return listOf(defaultItem) + options
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
package com.cursointermedio.myapplication.ui.addExercise.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.DialogFragment
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.ExerciseEntity
import com.cursointermedio.myapplication.databinding.DialogAddexerciseBinding
import com.cursointermedio.myapplication.databinding.DialogExerciseDescriptionBinding
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddExerciseDialog(
    private val onSaveClickListener: (ExerciseModel) -> Unit,
    private val categories:List<CategoryInfo>
) : BottomSheetDialogFragment() {
    private var _binding: DialogAddexerciseBinding? = null
    private val binding get() = _binding!!

    private var categoryId = CategoryInfo.ExerciseOthers.id


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogAddexerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        initUi()
        initListeners()

    }

    private fun initUi() {
        binding.tvSelectedCategory.text = getString(CategoryInfo.ExerciseOthers.name)

    }

    private fun initListeners(){
        binding.cvSave.setupTouchAction {
            val name = binding.etExerciseName.text.toString()
            if(name.isNotBlank()){
                val exercise = ExerciseModel(null, null, categoryId, name)

                onSaveClickListener(exercise)
                dialog?.dismiss()
            }
        }

        binding.categoryCard.setOnClickListener {
            val popup = PopupMenu(requireContext(), it)
            categories.forEachIndexed { index, category ->
                popup.menu.add(Menu.NONE, index, index, getString(category.name) )
            }


            popup.setOnMenuItemClickListener { menuItem ->
                val selectedCategory = categories[menuItem.itemId]
                binding.tvSelectedCategory.text = getString(selectedCategory.name)

                true
            }
            popup.show()
        }
    }

    private fun saveExercise(){
        var exercise:ExerciseModel
//        val category = binding.tvSelectedCategory.
    }

    @SuppressLint("UseGetLayoutInflater")
    private fun showCategoryPopup(anchorView: View) {
        val inflater = LayoutInflater.from(requireContext())
        val layout = inflater.inflate(R.layout.popup_category_menu, null) as LinearLayout

        // 👇 Declaramos popupWindow primero para usarlo luego en el setOnClickListener
        val popupWindow = PopupWindow(
            layout,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        categories.forEachIndexed { index, category ->
            val item = TextView(requireContext()).apply {
                text = getString(category.name)
                textSize = 16f
                setPadding(24, 24, 24, 24)
                setTextColor(Color.BLACK)
//                background = ContextCompat.getDrawable(requireContext(), android.R.drawable.list_selector_background)

                setOnClickListener {
                    binding.tvSelectedCategory.text = getString(category.name)
                    categoryId = category.id
                    popupWindow.dismiss() // 👈 Ya está accesible aquí
                }
            }

            layout.addView(item)

            if (index != categories.lastIndex) {
                val separator = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                    ).apply {
                        setMargins(24, 0, 24, 0)
                    }
                    setBackgroundColor(Color.LTGRAY)
                }
                layout.addView(separator)
            }
        }

        popupWindow.animationStyle = R.style.MenuTRainingPopupFadeAnimation
        popupWindow.showAsDropDown(anchorView, 0, -1500)
        popupWindow.showAsDropDown(view, 450, -630)

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
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            it.background = null
            behavior.isDraggable = true
        }
    }
}
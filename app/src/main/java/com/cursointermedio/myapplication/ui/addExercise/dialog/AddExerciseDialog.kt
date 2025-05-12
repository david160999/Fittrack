package com.cursointermedio.myapplication.ui.addExercise.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
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
import com.cursointermedio.myapplication.domain.model.CategoryInfo
import com.cursointermedio.myapplication.domain.model.ExerciseModel

class AddExerciseDialog(
    private val onSaveClickListener: (ExerciseModel) -> Unit,
    private val categories:List<CategoryInfo>
) : DialogFragment() {
    private var _binding: DialogAddexerciseBinding? = null
    private val binding get() = _binding!!

    private var categoryId = CategoryInfo.ExerciseOthers.id

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddexerciseBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context, R.style.DialogAnimationStyle)
        builder.setView(binding.root)
        val dialog = builder.create()


        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


//      setUpPopupMenuCategory()
        setOnTouchListener(binding, dialog!!)
        binding.categoryCard.setOnClickListener {
            showCategoryPopup(binding.categoryCard)

        }


        return dialog
    }

    private fun setUpPopupMenuCategory(){
        binding.tvSelectedCategory.text = getString(CategoryInfo.ExerciseOthers.name)

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

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListener(binding: DialogAddexerciseBinding, dialog: Dialog) {
        binding.cvSave.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    binding.cvSave.alpha = 0.2F
                }

                android.view.MotionEvent.ACTION_MOVE -> {}
                android.view.MotionEvent.ACTION_UP -> {
                    binding.cvSave.alpha = 1F
                    val x = event.x
                    val y = event.y
                    if (x >= 0 && x <= v.width && y >= 0 && y <= v.height) {
                        val name = binding.etExerciseName.text.toString().ifBlank {
                            binding.textInputLayout.hint.toString()
                        }
                        val exercise = ExerciseModel(null, null, categoryId, name)

                        onSaveClickListener(exercise)
                        dialog.dismiss()
                    }
                }

                android.view.MotionEvent.ACTION_CANCEL -> {
                    binding.cvSave.alpha = 1F
                }
            }
            true
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
                background = ContextCompat.getDrawable(requireContext(), android.R.drawable.list_selector_background)

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

        popupWindow.showAsDropDown(anchorView, 0, -1500)
    }
}
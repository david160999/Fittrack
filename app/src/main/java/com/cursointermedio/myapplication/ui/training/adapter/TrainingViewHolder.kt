package com.cursointermedio.myapplication.ui.training.adapter

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines
import com.cursointermedio.myapplication.databinding.ItemTrainingBinding
import com.cursointermedio.myapplication.domain.model.toDomain
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction

class TrainingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemTrainingBinding.bind(view)

    fun bind(
        trainingItemResponse: TrainingWithWeeksAndRoutines,
        onItemSelected: (Long) -> Unit,
        highlightAll: Boolean,
        menuActions: TrainingMenuActions,
        onHighlightRequest: () -> Unit
    ) {
        val context = binding.root.context

        val numWeeks = trainingItemResponse.weekWithRoutinesList.size
        val numRoutines =
            trainingItemResponse.weekWithRoutinesList.lastOrNull()?.routineList?.size ?: 0

        binding.tvTitle.text = trainingItemResponse.training.name
        binding.tvNumTrainings.text = context.getString(R.string.training_numDays, numRoutines)
        binding.tvNumCurrentsWeeks.text = context.getString(R.string.training_numWeeks, numWeeks)

        binding.root.setupTouchAction {
            onItemSelected(trainingItemResponse.training.trainingId!!)
        }

        // Fondo según highlight
//        val bgColorRes = if (highlightAll) R.color.backgroundMenu else android.R.color.white
//        binding.root.setBackgroundColor(ContextCompat.getColor(context, bgColorRes))

        binding.ivTrainingOptions.setupTouchAction {
            showMenu(
                binding.root,
                context,
                trainingItemResponse,
                menuActions
            ) { onHighlightRequest() }
        }

    }

    private fun showMenu(
        view: View,
        context: Context,
        trainingItemResponse: TrainingWithWeeksAndRoutines,
        menuActions: TrainingMenuActions,
        onHighlightRequest: () -> Unit
    ) {

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
            ContextCompat.getString(context, R.string.training_menuOption1),
            ContextCompat.getString(context, R.string.training_menuOption2),
            ContextCompat.getString(context, R.string.training_menuOption3),
            ContextCompat.getString(context, R.string.training_menuOption4),
        )

        val adapter = TrainingMenuAdapter(items) { position ->
            when (position) {
                0 -> changeName { menuActions.onChangeName(trainingItemResponse.training.toDomain()) }
                1 -> menuActions.onCopy(trainingItemResponse)
                2 -> menuActions.onShare(trainingItemResponse.training.toDomain())
                3 -> menuActions.onEliminate(trainingItemResponse.training.toDomain())
            }
            popupWindow.dismiss()
        }

        recyclerView.adapter = adapter
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 24f

//        popupWindow.setOnDismissListener {
//            onHighlightRequest()
//        }

        popupWindow.showAsDropDown(view, 450, -30)
        onHighlightRequest()
    }

    private fun changeName(onSave: ()->Unit){
        binding.ivTrainingOptions.visibility = View.GONE
        binding.ivTrainingSave.visibility = View.VISIBLE

        val name = binding.tvTitle.text
        binding.editTextTitle.hint = name

        binding.tvTitle.visibility = View.GONE
        binding.editTextTitle.visibility = View.VISIBLE




    }


}


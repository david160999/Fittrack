package com.cursointermedio.myapplication.ui.training.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.TrainingWithWeeksAndRoutines

class TrainingAdapter(
    private var trainingWithWeeksAndRoutinesList: List<TrainingWithWeeksAndRoutines> = mutableListOf(),
    private val onItemSelected: (Long) -> Unit,
    private val menuActions: TrainingMenuActions

) : RecyclerView.Adapter<TrainingViewHolder>() {

    private var highlightAll = false

    fun highlightAllItems() {
        highlightAll = !highlightAll
        notifyDataSetChanged()
    }

    fun updateList(trainingList: List<TrainingWithWeeksAndRoutines>) {
        this.trainingWithWeeksAndRoutinesList = trainingList
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        return TrainingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_training, parent, false)
        )
    }


    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        holder.bind(trainingWithWeeksAndRoutinesList[position], onItemSelected, highlightAll, menuActions){
            highlightAllItems()
        }

    }

    override fun getItemCount() = trainingWithWeeksAndRoutinesList.size


}


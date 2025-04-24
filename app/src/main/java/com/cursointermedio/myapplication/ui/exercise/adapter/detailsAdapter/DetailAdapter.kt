package com.cursointermedio.myapplication.ui.exercise.adapter.detailsAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R

class DetailAdapter(
    private var exerciseList: List<String> = emptyList(),
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<DetailViewHolder>() {

    fun updateList(exerciseList: List<String>) {
        this.exerciseList = exerciseList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_details, parent, false)
        )
    }


    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(exerciseList[position], onItemSelected)
    }

    override fun getItemCount() = exerciseList.size
}
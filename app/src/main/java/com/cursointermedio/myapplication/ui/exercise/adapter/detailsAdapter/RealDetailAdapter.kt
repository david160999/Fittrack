package com.cursointermedio.myapplication.ui.exercise.adapter.detailsAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.domain.model.DetailModel

class RealDetailAdapter(
    private val onItemChanged: (DetailModel) -> Unit,
    private val onItemChangedFragment: (Int) -> Unit
) : ListAdapter<DetailModel, RealDetailViewHolder>(DetailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_details, parent, false)
        return RealDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: RealDetailViewHolder, position: Int) {
        holder.bind(getItem(position), onItemChanged, onItemChangedFragment)
    }

    class DetailDiffCallback : DiffUtil.ItemCallback<DetailModel>() {
        override fun areItemsTheSame(oldItem: DetailModel, newItem: DetailModel): Boolean {
            return oldItem.detailsId == newItem.detailsId
        }

        override fun areContentsTheSame(oldItem: DetailModel, newItem: DetailModel): Boolean {
            return oldItem == newItem
        }
    }
}
package com.cursointermedio.myapplication.ui.exercise.adapter.detailsAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.domain.model.DetailModel

class ObjDetailAdapter(
    private val onItemChanged: (DetailModel) -> Unit,
    private val onItemChangedFragment: (Int) -> Unit
) : ListAdapter<DetailModel, ObjDetailViewHolder>(DetailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_obj_details, parent, false)
        return ObjDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObjDetailViewHolder, position: Int) {
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
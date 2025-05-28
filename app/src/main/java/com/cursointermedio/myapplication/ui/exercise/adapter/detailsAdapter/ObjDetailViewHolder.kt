package com.cursointermedio.myapplication.ui.exercise.adapter.detailsAdapter

import android.annotation.SuppressLint
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.ItemDetailsBinding
import com.cursointermedio.myapplication.databinding.ItemObjDetailsBinding
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction

class ObjDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemObjDetailsBinding.bind(view)

    @SuppressLint("ClickableViewAccessibility", "PrivateResource", "SetTextI18n")
    fun bind(
        detail: DetailModel, onItemChanged: (DetailModel) -> Unit, onItemChangedFragment: (Int) -> Unit
    ) {
        initUi(detail, onItemChangedFragment)
        initListeners(detail, onItemChanged)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUi(detail: DetailModel, onItemChangedFragment: (Int) -> Unit) {
        if (detail.realWeight != null && detail.realReps != null && detail.realRpe != null) {
            binding.tvCardView1.visibility = View.VISIBLE
            binding.ivCardView1.visibility = View.GONE
            binding.tvCardView1.text = "${detail.realWeight} x ${detail.realReps} @ ${detail.realRpe}"

            binding.ivCardView1.visibility = View.GONE
            binding.tvCardView1.visibility = View.VISIBLE

            binding.cv4.setOnTouchListener(null)
        } else {
            binding.tvCardView1.visibility = View.GONE
            binding.ivCardView1.visibility = View.VISIBLE

            binding.cv4.setupTouchAction {
                onItemChangedFragment(1)
            }
        }

        detail.objWeight?.let { binding.etWeight.setText(it.toString()) }
        detail.objReps?.let { binding.etReps.setText(it.toString()) }
        detail.objRpe?.let { binding.etRpe.setText(it.toString()) }
    }

    private fun initListeners(detail: DetailModel, onItemChanged: (DetailModel) -> Unit) {
        var currentDetail = detail

        binding.etWeight.doAfterTextChanged { text ->
            val newWeight = text.toString().toIntOrNull()
            if (newWeight != currentDetail.objWeight) {
                currentDetail = currentDetail.copy(objWeight = newWeight)
                onItemChanged(currentDetail)
            }
        }

        binding.etReps.doAfterTextChanged { text ->
            val newReps = text.toString().toIntOrNull()
            if (newReps != currentDetail.objReps) {
                currentDetail = currentDetail.copy(objReps = newReps)
                onItemChanged(currentDetail)
            }
        }

        binding.etRpe.doAfterTextChanged { text ->
            val newRpe = text.toString().toIntOrNull()
            if (newRpe != currentDetail.objRpe) {
                currentDetail = currentDetail.copy(objRpe = newRpe)
                onItemChanged(currentDetail)
            }
        }

    }
}

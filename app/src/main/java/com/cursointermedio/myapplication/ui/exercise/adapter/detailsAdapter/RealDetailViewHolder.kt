package com.cursointermedio.myapplication.ui.exercise.adapter.detailsAdapter

import android.annotation.SuppressLint
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.databinding.ItemDetailsBinding
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction

class RealDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemDetailsBinding.bind(view)

    @SuppressLint("ClickableViewAccessibility", "PrivateResource", "SetTextI18n")
    fun bind(
        detail: DetailModel, onItemChanged: (DetailModel) -> Unit,onItemChangedFragment: (Int) -> Unit
    ) {
        initUi(detail, onItemChangedFragment)
        initListeners(detail, onItemChanged)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUi(detail: DetailModel, onItemChangedFragment: (Int) -> Unit) {
        if (detail.objWeight != null && detail.objReps != null && detail.objRpe != null) {
            binding.tvCardView1.visibility = View.VISIBLE
            binding.ivCardView1.visibility = View.GONE
            binding.tvCardView1.text = "${detail.objWeight} x ${detail.objReps} @ ${detail.objRpe}"

            binding.ivCardView1.visibility = View.GONE
            binding.tvCardView1.visibility = View.VISIBLE

            binding.cv1.setOnTouchListener(null)
        } else {
            binding.tvCardView1.visibility = View.GONE
            binding.ivCardView1.visibility = View.VISIBLE

            binding.cv1.setupTouchAction {
                onItemChangedFragment(0)
            }
        }

        detail.realWeight?.let { binding.etWeight.setText(it.toString()) }
        detail.realReps?.let { binding.etReps.setText(it.toString()) }
        detail.realRpe?.let { binding.etRpe.setText(it.toString()) }
    }

    private fun initListeners(detail: DetailModel, onItemChanged: (DetailModel) -> Unit) {
        var currentDetail = detail

        binding.etWeight.doAfterTextChanged { text ->
            val newWeight = text.toString().toIntOrNull()
            if (newWeight != currentDetail.realWeight) {
                currentDetail = currentDetail.copy(realWeight = newWeight)
                onItemChanged(currentDetail)
            }
        }

        binding.etReps.doAfterTextChanged { text ->
            val newReps = text.toString().toIntOrNull()
            if (newReps != currentDetail.realReps) {
                currentDetail = currentDetail.copy(realReps = newReps)
                onItemChanged(currentDetail)
            }
        }

        binding.etRpe.doAfterTextChanged { text ->
            val newRpe = text.toString().toIntOrNull()
            if (newRpe != currentDetail.realRpe) {
                currentDetail = currentDetail.copy(realRpe = newRpe)
                onItemChanged(currentDetail)
            }
        }

    }
}

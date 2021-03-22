package com.test.drawableloadersample

import androidx.recyclerview.widget.RecyclerView
import com.test.drawableloadersample.databinding.ItemDrawableBinding

class DrawableItemViewHolder(private val binding: ItemDrawableBinding) :
    RecyclerView.ViewHolder(binding.root)  {

    fun bind(drawableName: String, onItemClicked: (Int) -> Unit) {

        binding.tvDrawableName.text = drawableName

        binding.btnShowImg.setOnClickListener {
            onItemClicked(adapterPosition)
        }

        binding.root.setOnClickListener {
            onItemClicked(adapterPosition)
        }

    }
}
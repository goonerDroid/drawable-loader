@file:Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")

package com.test.drawableloadersample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.drawableloadersample.databinding.ItemDrawableBinding

class DrawableAdapter (private val drawableMap:HashMap<String,Int>,
                       private val onItemClicked: (Int) -> Unit) :
    RecyclerView.Adapter<DrawableItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DrawableItemViewHolder (
        ItemDrawableBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: DrawableItemViewHolder, position: Int) {
        holder.bind(drawableMap.keys.toTypedArray()[holder.adapterPosition], onItemClicked)
    }

    override fun getItemCount(): Int {
        return drawableMap.size
    }
}
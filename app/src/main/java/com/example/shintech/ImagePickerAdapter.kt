package com.example.shintech

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class ImagePickerAdapter(
    private val imageList: List<Int>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ImagePickerAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageResId = imageList[position]
        holder.bind(imageResId)
        holder.itemView.setOnClickListener { onItemClick(imageResId) }
    }

    override fun getItemCount(): Int = imageList.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.iv_picker_image)

        fun bind(imageResId: Int) {
            imageView.load(imageResId)
        }
    }
}

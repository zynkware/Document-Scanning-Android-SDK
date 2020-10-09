package com.zynksoftware.documentscannersample.adapters

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.chrisbanes.photoview.PhotoView
import com.zynksoftware.documentscannersample.R
import java.io.File

class ImageViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val photoImageView = itemView.findViewById<PhotoView>(R.id.photoImageView)
    private val saveButton = itemView.findViewById<TextView>(R.id.saveButton)

    fun bindData(context: Context?, image: File, listener: ImageAdapterListener, position: Int, size: Int) {
        photoImageView.setImageFile(image)
        saveButton.setOnClickListener {
            listener.onSaveButtonClicked(image)
        }
    }
}

private fun PhotoView.setImageFile(image: File) {
    Glide.with(this).load(image)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .into(this)
}

package me.ibore.image.picker.adapter

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import me.ibore.databinding.XItemImagePickerPreviewSelectBinding
import me.ibore.image.picker.model.MediaFile
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.RecyclerHolder

class ImageSelectAdapter : BindingAdapter<XItemImagePickerPreviewSelectBinding, MediaFile>() {

    var currentPosition: Int = -1

    override fun XItemImagePickerPreviewSelectBinding.onBindHolder(
        holder: RecyclerHolder, data: MediaFile, dataPosition: Int
    ) {
        Glide.with(ivImageSelect).load(data.path).into(ivImageSelect)
        if (currentPosition == dataPosition) {
            viewImageSelect.isVisible = true
        } else {
            viewImageSelect.isGone = true
        }
    }

}
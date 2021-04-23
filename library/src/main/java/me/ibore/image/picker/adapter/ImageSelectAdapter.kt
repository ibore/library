package me.ibore.image.picker.adapter

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import me.ibore.databinding.ItemImagePickerPreviewSelectBinding
import me.ibore.image.picker.model.MediaFile
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.RecyclerHolder

class ImageSelectAdapter : BindingAdapter<ItemImagePickerPreviewSelectBinding, MediaFile>() {

    var currentPosition: Int = -1

    override fun ItemImagePickerPreviewSelectBinding.onBindHolder(
        holder: RecyclerHolder, data: MediaFile, dataPosition: Int
    ) {
        Glide.with(ivImageSelect).load(data.path).into(ivImageSelect)
        if (currentPosition == dataPosition) {
            ivImageSelect.isVisible = true
        } else {
            ivImageSelect.isGone = true
        }
    }

}
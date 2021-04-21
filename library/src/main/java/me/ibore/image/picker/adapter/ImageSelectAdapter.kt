package me.ibore.image.picker.adapter

import me.ibore.databinding.ItemImagePickerPreviewSelectBinding
import me.ibore.image.picker.model.MediaFile
import me.ibore.recycler.adapter.BindingAdapter

class ImageSelectAdapter : BindingAdapter<ItemImagePickerPreviewSelectBinding, MediaFile>() {

    var currentPosition: Int = -1

    override fun ItemImagePickerPreviewSelectBinding.onBindingHolder(
        holder: BindingHolder<ItemImagePickerPreviewSelectBinding>,
        data: MediaFile,
        dataPosition: Int
    ) {
//        holder.viewHolder.image(R.id.iv_image_select, data.path)
//        if (currentPosition == dataPosition) {
//            holder.viewHolder.visibility(R.id.view_image_select, View.VISIBLE)
//        } else {
//            holder.viewHolder.visibility(R.id.view_image_select, View.GONE)
//        }
    }

}
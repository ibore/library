package me.ibore.image.picker.adapter

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import me.ibore.R
import me.ibore.databinding.ItemImagePickerFolderBinding
import me.ibore.image.picker.model.MediaFolder
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.RecyclerHolder

class ImageFoldersAdapter : BindingAdapter<ItemImagePickerFolderBinding, MediaFolder>() {

    private var currentPosition: Int = 0

    override fun ItemImagePickerFolderBinding.onBindHolder(
        holder: RecyclerHolder, data: MediaFolder, dataPosition: Int
    ) {
        val imageSize: Int = data.mediaFileList.size
        tvImagePickerFolder.text = data.folderName
        tvImagePickerNumber.text = holder.context.getString(R.string.image_picker_num, imageSize)
        clItem.setOnClickListener {
            currentPosition = dataPosition
            onItemClickListener?.onItemClick(holder, data, dataPosition)
            notifyDataSetChanged()
        }
        if (currentPosition == dataPosition) {
            ivImagePickerFolderCheck.isVisible = true
        } else {
            ivImagePickerFolderCheck.isGone = true
        }
        Glide.with(ivImagePickerFolder).load(data.folderCover).into(ivImagePickerFolder)
    }

}
package me.ibore.image.picker.adapter

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import me.ibore.R
import me.ibore.databinding.XItemImagePickerFolderBinding
import me.ibore.image.picker.model.MediaFolder
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.RecyclerHolder

class ImageFoldersAdapter : BindingAdapter<XItemImagePickerFolderBinding, MediaFolder>() {

    private var currentPosition: Int = 0

    override fun XItemImagePickerFolderBinding.onBindHolder(
        holder: RecyclerHolder, data: MediaFolder, dataPosition: Int
    ) {
        val imageSize: Int = data.mediaFileList.size
        tvImageFolder.text = data.folderName
        tvImageNumber.text = holder.string(R.string.image_picker_num, imageSize)
        clItem.setOnClickListener {
            currentPosition = dataPosition
            onItemClickListener?.onItemClick(holder, data, dataPosition)
            notifyDataSetChanged()
        }
        if (currentPosition == dataPosition) {
            ivImageFolderCheck.isVisible = true
        } else {
            ivImageFolderCheck.isGone = true
        }
        Glide.with(ivImageFolder).load(data.folderCover).into(ivImageFolder)
    }

}
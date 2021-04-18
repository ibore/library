package me.ibore.image.picker.adapter

import me.ibore.databinding.ItemImagePickerFolderBinding
import me.ibore.image.picker.model.MediaFolder
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.BindingHolder

class ImageFoldersAdapter : BindingAdapter<ItemImagePickerFolderBinding, MediaFolder>() {

    private var currentPosition: Int = 0

    override fun onBindingHolder(
        holder: BindingHolder<ItemImagePickerFolderBinding>,
        binding: ItemImagePickerFolderBinding,
        data: MediaFolder,
        dataPosition: Int
    ) {
        /*val imageSize = data.mediaFileList.size
        holder.viewHolder.text(R.id.tv_image_picker_folder, data.folderName)
        holder.viewHolder.text(R.id.tv_image_picker_number, String.format(holder.viewHolder.context().getString(R.string.image_picker_num), imageSize))
        holder.viewHolder.onClickListener(R.id.cl_item) {
            currentPosition = dataPosition
            holder.viewHolder.visibility(R.id.iv_image_picker_folder_check, View.VISIBLE)
            onItemClickListener?.onItemClick(holder, data, dataPosition)
            notifyDataSetChanged()
        }
        if (currentPosition == dataPosition) {
            holder.viewHolder.visibility(R.id.iv_image_picker_folder_check, View.VISIBLE)
        } else {
            holder.viewHolder.visibility(R.id.iv_image_picker_folder_check, View.GONE)
        }
        holder.viewHolder.image(R.id.iv_image_picker_folder, data.folderCover)*/
    }

}
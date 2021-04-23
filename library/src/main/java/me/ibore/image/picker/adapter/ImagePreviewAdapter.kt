package me.ibore.image.picker.adapter

import android.content.Intent
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import me.ibore.databinding.ItemImagePickerPreviewBinding
import me.ibore.image.picker.model.MediaFile
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.utils.UriUtils
import java.io.File

class ImagePreviewAdapter : BindingAdapter<ItemImagePickerPreviewBinding, MediaFile>() {

    override fun ItemImagePickerPreviewBinding.onBindHolder(
        holder: RecyclerHolder, data: MediaFile, dataPosition: Int
    ) {
        ivPickerImage.reset()
        Glide.with(ivPickerImage).load(data.path).into(ivPickerImage)
        onItemChildClickListener?.apply {
            ivPickerImage.setOnClickListener {
                onItemClick(holder, it.id, data, dataPosition)
            }
        }
        if (data.duration > 0) {
            ivImagePickerPlay.isVisible = true
        } else {
            ivImagePickerPlay.isGone = true
        }
        ivImagePickerPlay.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = UriUtils.file2Uri(File(data.path))
            intent.setDataAndType(uri, "video/*")
            //给所有符合跳转条件的应用授权
//            val resInfoList = packageManager
//                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
//            for (resolveInfo in resInfoList) {
//                val packageName = resolveInfo.activityInfo.packageName
//                grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
//                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            }
            holder.context.startActivity(intent)
        }
    }
}
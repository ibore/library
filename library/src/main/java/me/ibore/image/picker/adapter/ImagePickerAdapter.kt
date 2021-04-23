package me.ibore.image.picker.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.ibore.R
import me.ibore.databinding.ItemImagePickerBinding
import me.ibore.image.picker.ImagePicker
import me.ibore.image.picker.model.MediaFile
import me.ibore.image.picker.utils.ImagePickerUtils
import me.ibore.ktx.dp2px
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.utils.ScreenUtils
import java.text.SimpleDateFormat
import java.util.*

class ImagePickerAdapter : BindingAdapter<ItemImagePickerBinding, MediaFile>() {

    override fun onCreateHolder(parent: ViewGroup, dataType: Int): RecyclerHolder {
        val holder = super.onCreateHolder(parent, dataType)
        holder.itemView.updateLayoutParams<RecyclerView.LayoutParams> {
            height = (ScreenUtils.appScreenHeight - dp2px(10F)) / 4
        }
        return holder
    }

    override fun ItemImagePickerBinding.onBindHolder(
        holder: RecyclerHolder, data: MediaFile, dataPosition: Int
    ) {
        tvPickerTakePhoto.isGone = true
        tvPickerCheck.isVisible = true
        ivPickerCheck.isVisible = true
        val indexOf = ImagePickerUtils.indexOfSelect(data)
        if (indexOf >= 0) {
            tvPickerCheck.text = (indexOf + 1).toString()
            ivPickerCheck.setImageResource(R.drawable.image_picker_checked)
            viewMask.isVisible = true
        } else {
            tvPickerCheck.text = ""
            ivPickerCheck.setImageResource(R.drawable.image_picker_check)
            viewMask.isGone = true
        }
        Glide.with(ivPickerImage).load(data.path).into(ivPickerImage)
        if (data.duration > 0) {
            //如果是视频，需要显示视频时长
            tvPickerVideoDuration.text = getVideoDuration(data.duration)
            tvPickerVideoDuration.isVisible = true
            ivPickerGif.isGone = true
        } else {
            //如果是gif图，显示gif标识
            val suffix = data.path.substring(data.path.lastIndexOf(".") + 1)
            if (suffix.toUpperCase(Locale.ROOT) == "GIF") {
                ivPickerGif.isVisible = true
            } else {
                ivPickerGif.isGone = true
            }
            tvPickerVideoDuration.isGone = true
        }
        sclItem.setOnClickListener { onMediaListener?.onMediaClick(it, data, dataPosition) }
        ivPickerCheck.setOnClickListener { onMediaListener?.onMediaCheck(it, data, dataPosition) }
//        if (ImagePicker.getConfig().showCamera && dataPosition == 0) {
//            tvPickerTakePhoto.isVisible = true
//            tvPickerCheck.isGone = true
//            ivPickerCheck.isGone = true
//            ivPickerGif.isGone = true
//            tvPickerVideoDuration.isGone = true
//            ivPickerImage.setImageDrawable(ColorDrawable(holder.color(R.color.image_picker_bar_color)))
//            sclItem.setOnClickListener { onMediaListener?.onCameraClick() }
//        } else {
//
//        }
    }

    fun getDifference(): Int {
        return 0
//        return (if (ImagePicker.getConfig().showCamera) 1 else 0)
    }

    // 获取视频时长（格式化）
    @SuppressLint("SimpleDateFormat")
    private fun getVideoDuration(timestamp: Long): String? {
        if (timestamp < 1000) {
            return "00:01"
        }
        val date = Date(timestamp)
        val simpleDateFormat = SimpleDateFormat("mm:ss")
        return simpleDateFormat.format(date)
    }

    var onMediaListener: OnMediaListener? = null

    interface OnMediaListener {
        fun onCameraClick()
        fun onMediaClick(view: View, data: MediaFile, dataPosition: Int)
        fun onMediaCheck(view: View, data: MediaFile, dataPosition: Int)
    }


}
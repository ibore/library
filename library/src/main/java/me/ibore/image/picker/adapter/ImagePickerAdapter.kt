package me.ibore.image.picker.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import me.ibore.R
import me.ibore.image.picker.ImagePicker
import me.ibore.image.picker.model.MediaFile
import me.ibore.ktx.dp2px
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.utils.ScreenUtils
import java.text.SimpleDateFormat
import java.util.*

class ImagePickerAdapter : RecyclerView.Adapter<RecyclerHolder>() {

    private var datas: MutableList<MediaFile>? = null

    fun setDatas(datas: MutableList<MediaFile>) {
        this.datas = datas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        val holder = RecyclerHolder.create(parent, R.layout.item_image_picker)
        holder.itemView.updateLayoutParams<RecyclerView.LayoutParams> {
            height = (ScreenUtils.appScreenHeight - dp2px(10F)) / 4
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
//        val viewHolder = holder.viewHolder
//        if (ImagePicker.getConfig().showCamera && position == 0) {
//            viewHolder.visibility(R.id.tv_picker_take_photo, View.VISIBLE)
//            viewHolder.visibility(R.id.iv_picker_check, View.GONE)
//            viewHolder.visibility(R.id.tv_picker_check, View.GONE)
//            viewHolder.visibility(R.id.iv_picker_gif, View.GONE)
//            viewHolder.visibility(R.id.tv_picker_video_duration, View.GONE)
//            viewHolder.imageDrawable(R.id.iv_picker_image, ColorDrawable(ContextCompat.getColor(viewHolder.context(), R.color.image_picker_bar_color)))
//            viewHolder.onClickListener(R.id.scl_item) {
//                onMediaListener?.onCameraClick()
//            }
//        } else {
//            viewHolder.visibility(R.id.tv_picker_take_photo, View.GONE)
//            viewHolder.visibility(R.id.iv_picker_check, View.VISIBLE)
//            viewHolder.visibility(R.id.tv_picker_check, View.VISIBLE)
//            val dataPosition = position - getDifference()
//            val data = datas!![dataPosition]
//            val indexOf = ImagePickerUtils.indexOfSelect(data)
//            if (indexOf >= 0) {
//                viewHolder.text(R.id.tv_picker_check, (indexOf + 1).toString())
//                viewHolder.imageResource(R.id.iv_picker_check, R.drawable.image_picker_checked)
//            } else {
//                viewHolder.text(R.id.tv_picker_check, "")
//                viewHolder.imageResource(R.id.iv_picker_check, R.drawable.image_picker_check)
//            }
//            viewHolder.image(R.id.iv_picker_image, data.path)
//            if (data.duration > 0) {
//                //如果是视频，需要显示视频时长
//                viewHolder.text(R.id.tv_picker_video_duration, getVideoDuration(data.duration))
//                viewHolder.visibility(R.id.tv_picker_video_duration, View.VISIBLE)
//                viewHolder.visibility(R.id.iv_picker_gif, View.GONE)
//            } else {
//                //如果是gif图，显示gif标识
//                val suffix = data.path.substring(data.path.lastIndexOf(".") + 1)
//                if (suffix.toUpperCase(Locale.ROOT) == "GIF") {
//                    viewHolder.visibility(R.id.iv_picker_gif, View.VISIBLE)
//                } else {
//                    viewHolder.visibility(R.id.iv_picker_gif, View.GONE)
//                }
//                viewHolder.visibility(R.id.tv_picker_video_duration, View.GONE)
//            }
//            viewHolder.onClickListener(R.id.scl_item) {
//                onMediaListener?.onMediaClick(it, data, dataPosition)
//            }
//            viewHolder.onClickListener(R.id.iv_picker_check) {
//                onMediaListener?.onMediaCheck(it, data, dataPosition)
//            }
//        }
    }

    override fun getItemCount(): Int {
        return if (!datas.isNullOrEmpty()) {
            datas!!.size + getDifference()
        } else 0
    }

    fun getDifference(): Int {
        return (if (ImagePicker.getConfig().showCamera) 1 else 0)
    }

    fun getData(position: Int): MediaFile {
        return datas!![position]
    }

    fun getDatas(): MutableList<MediaFile>? {
        return datas
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
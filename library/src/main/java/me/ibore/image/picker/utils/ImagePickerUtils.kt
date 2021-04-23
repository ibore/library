package me.ibore.image.picker.utils

import android.annotation.SuppressLint
import android.widget.TextView
import me.ibore.R
import me.ibore.image.picker.ImagePicker
import me.ibore.image.picker.model.MediaFile
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object ImagePickerUtils {

    val selectMedias: ArrayList<MediaFile> = ArrayList()
    var maxCount = 1

    fun restSelect() {
        clearSelect()
        maxCount = ImagePicker.getConfig().maxCount
        /*val imagePaths = ImagePicker.getConfig().imagePaths
        if (!imagePaths.isNullOrEmpty()) {
            for (i in imagePaths.indices) {
                val imagePath = imagePaths[i]
                if (!selectMedias.contains(imagePath) && selectMedias.size < maxCount) {
                    selectMedias.add(imagePath)
                }
            }
        }*/
    }

    fun isSelectContains(mediaFile: MediaFile): Boolean {
        return selectMedias.contains(mediaFile)
    }

    fun clearSelect() {
        maxCount = 1
        selectMedias.clear()
    }

    fun isSelectOutRange(): Boolean {
        return selectMedias.size >= maxCount
    }

    fun addImageToSelectList(mediaFile: MediaFile): Boolean {
        return if (selectMedias.contains(mediaFile)) {
            selectMedias.remove(mediaFile)
        } else {
            if (selectMedias.size < maxCount) {
                selectMedias.add(mediaFile)
            } else {
                false
            }
        }
    }

    fun updateCommitView(commitView: TextView) {
        val selectCount = selectMedias.size
        if (selectCount == 0) {
            commitView.isEnabled = false
            commitView.text = commitView.context.getString(R.string.image_picker_confirm)
            return
        }
        if (selectCount <= maxCount) {
            commitView.isEnabled = true
            commitView.text = String.format(commitView.context.getString(R.string.image_picker_confirm_msg), selectCount, maxCount)
            return
        }
    }

    fun isCanAddSelectionPaths(currentPath: MediaFile, filePath: MediaFile): Boolean {
        return !(MediaFileUtil.isVideoFileType(currentPath.path)
                && !MediaFileUtil.isVideoFileType(filePath.path) || !MediaFileUtil.isVideoFileType(currentPath.path)
                && MediaFileUtil.isVideoFileType(filePath.path))
    }

    fun indexOfSelect(mediaFile: MediaFile): Int {
        return selectMedias.indexOf(mediaFile)
    }

    fun removeSelect(mediaFile: MediaFile): Boolean {
        return selectMedias.remove(mediaFile)
    }

    fun addSelect(mediaFile: MediaFile) {
        selectMedias.add(mediaFile)
    }

    fun getSelectPaths(): ArrayList<String> {
        val selectPaths =  ArrayList<String>()
        for (mediaFile in selectMedias) {
            selectPaths.add(mediaFile.path)
        }
        return selectPaths
    }

    fun updatePreviewView(preview: TextView) {
        val selectCount = selectMedias.size
        if (selectCount == 0) {
            preview.isEnabled = false
            preview.text = preview.context.getString(R.string.image_picker_preview)
            return
        }
        if (selectCount <= maxCount) {
            preview.isEnabled = true
            preview.text = String.format(preview.context.getString(R.string.image_picker_preview_msg), selectCount)
            return
        }
    }


    @SuppressLint("SimpleDateFormat")
    fun getImageTime(timestamp: Long): String? {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.time = Date()
        val imageCalendar = Calendar.getInstance()
        imageCalendar.timeInMillis = timestamp
        return if (currentCalendar[Calendar.DAY_OF_YEAR] == imageCalendar[Calendar.DAY_OF_YEAR]
            && currentCalendar[Calendar.YEAR] == imageCalendar[Calendar.YEAR]) {
            "今天"
        } else if (currentCalendar[Calendar.WEEK_OF_YEAR] == imageCalendar[Calendar.WEEK_OF_YEAR]
            && currentCalendar[Calendar.YEAR] == imageCalendar[Calendar.YEAR]) {
            "本周"
        } else if (currentCalendar[Calendar.MONTH] == imageCalendar[Calendar.MONTH]
            && currentCalendar[Calendar.YEAR] == imageCalendar[Calendar.YEAR]) {
            "本月"
        } else {
            val date = Date(timestamp)
            val sdf = SimpleDateFormat("yyyy/MM")
            sdf.format(date)
        }
    }
}
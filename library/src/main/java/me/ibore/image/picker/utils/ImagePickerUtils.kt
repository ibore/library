package me.ibore.image.picker.utils

import android.annotation.SuppressLint
import android.content.Context
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

    fun addSelectList(mediaFile: MediaFile): Boolean {
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

    @SuppressLint("SimpleDateFormat")
    fun getImageTime(context: Context, timestamp: Long): String? {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.time = Date()
        val imageCalendar = Calendar.getInstance()
        imageCalendar.timeInMillis = timestamp
        return if (currentCalendar[Calendar.DAY_OF_YEAR] == imageCalendar[Calendar.DAY_OF_YEAR]
            && currentCalendar[Calendar.YEAR] == imageCalendar[Calendar.YEAR]) {
            return context.getString(R.string.today)
        } else if (currentCalendar[Calendar.WEEK_OF_YEAR] == imageCalendar[Calendar.WEEK_OF_YEAR]
            && currentCalendar[Calendar.YEAR] == imageCalendar[Calendar.YEAR]) {
            return context.getString(R.string.this_week)
        } else if (currentCalendar[Calendar.MONTH] == imageCalendar[Calendar.MONTH]
            && currentCalendar[Calendar.YEAR] == imageCalendar[Calendar.YEAR]) {
            return context.getString(R.string.this_month)
        } else {
            val date = Date(timestamp)
            val sdf = SimpleDateFormat("yyyy/MM")
            sdf.format(date)
        }
    }
}
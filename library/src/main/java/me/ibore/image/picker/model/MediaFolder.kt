package me.ibore.image.picker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaFolder(var folderId: Int,
                       var folderName: String,
                       var folderCover: String,
                       var mediaFileList: MutableList<MediaFile>,
                       var isCheck: Boolean = false) : Parcelable
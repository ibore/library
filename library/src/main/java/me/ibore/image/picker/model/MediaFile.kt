package me.ibore.image.picker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaFile(val path: String,
                     val mime: String? = null,
                     val folderId: Int? = null,
                     val folderName: String? = null,
                     val duration: Long = 0,
                     val dateToken: Long = 0) : Parcelable
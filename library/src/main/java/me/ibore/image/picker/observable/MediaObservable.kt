package me.ibore.image.picker.observable

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import me.ibore.R
import me.ibore.image.picker.ImagePicker
import me.ibore.image.picker.model.MediaFile
import me.ibore.image.picker.model.MediaFolder
import me.ibore.utils.Utils
import java.util.*

class MediaObservable(private val context: Context) : ObservableOnSubscribe<MutableList<MediaFolder>> {

    companion object {
        private const val ALL_MEDIA_FOLDER = -1 //全部媒体
        private const val ALL_VIDEO_FOLDER = -2 //全部视频
    }

    override fun subscribe(emitter: ObservableEmitter<MutableList<MediaFolder>>) {
        var imageFiles: ArrayList<MediaFile>? = null
        var videoFiles: ArrayList<MediaFile>? = null
        if (ImagePicker.getConfig().showImage) {
            imageFiles = getImageMediaFiles()
        }
        if (ImagePicker.getConfig().showVideo) {
            videoFiles = getVideoMediaFiles()
        }
        emitter.onNext(getMediaFolder(Utils.app, imageFiles, videoFiles))
        emitter.onComplete()
    }

    private fun getVideoMediaFiles(): ArrayList<MediaFile> {
        val scanUri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection: Array<String> = arrayOf(
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATE_TAKEN)
        val selection = ""
        val selectionArgs: Array<String> = arrayOf()
        val order: String = MediaStore.Video.Media.DATE_TAKEN + " desc"
        val mediaFiles = ArrayList<MediaFile>()
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(scanUri, projection, selection, selectionArgs, order)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val mime = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE))
                val folderId = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
                val folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                val duration: Long = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                val dateToken = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN))
                mediaFiles.add(MediaFile(path, mime, folderId, folderName, duration, dateToken))
            }
            cursor.close()
        }
        return mediaFiles
    }

    private fun getImageMediaFiles(): ArrayList<MediaFile> {
        val scanUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection: Array<String> = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        )
        val selection: String = if (ImagePicker.getConfig().filterGif) {
            //过滤GIF
            MediaColumns.MIME_TYPE + "=? or " + MediaColumns.MIME_TYPE + "=?"
        } else {
            MediaColumns.MIME_TYPE + "=? or " + MediaColumns.MIME_TYPE + "=?" + " or " + MediaColumns.MIME_TYPE + "=?"
        }
        val selectionArgs: Array<String> = if (ImagePicker.getConfig().filterGif) {
            //过滤GIF
            arrayOf("image/jpeg", "image/png")
        } else {
            arrayOf("image/jpeg", "image/png", "image/gif")
        }
        val order: String = MediaStore.Images.Media.DATE_TAKEN + " desc"
        val mediaFiles = ArrayList<MediaFile>()
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(scanUri, projection, selection, selectionArgs, order)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                val mime =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
                val folderId =
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                val folderName =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val dateToken =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN))
                mediaFiles.add(MediaFile(path, mime, folderId, folderName, 0, dateToken))
            }
            cursor.close()
        }
        return mediaFiles
    }

    private fun getMediaFolder(
        context: Context, imageFileList: ArrayList<MediaFile>?, videoFileList: ArrayList<MediaFile>?
    ): MutableList<MediaFolder> {
        //根据媒体所在文件夹Id进行聚类（相册）
        val mediaFolderMap: MutableMap<Int, MediaFolder> = HashMap()
        //全部图片、视频文件
        val mediaFileList = ArrayList<MediaFile>()
        if (imageFileList != null) {
            mediaFileList.addAll(imageFileList)
        }
        if (videoFileList != null) {
            mediaFileList.addAll(videoFileList)
        }
        //对媒体数据进行排序
        mediaFileList.sortWith { o1, o2 ->
            when {
                o1.dateToken > o2.dateToken -> -1
                o1.dateToken < o2.dateToken -> 1
                else -> 0
            }
        }
        //全部图片或视频
        if (mediaFileList.isNotEmpty()) {
            val allMediaFolder =
                MediaFolder(
                    ALL_MEDIA_FOLDER, context.getString(R.string.image_picker_all_media),
                    mediaFileList[0].path, mediaFileList
                )
            mediaFolderMap[ALL_MEDIA_FOLDER] = allMediaFolder
        }
        //全部视频
        if (!videoFileList.isNullOrEmpty()) {
            val allVideoFolder =
                MediaFolder(
                    ALL_VIDEO_FOLDER, context.getString(R.string.image_picker_all_video),
                    videoFileList[0].path, videoFileList
                )
            mediaFolderMap[ALL_VIDEO_FOLDER] = allVideoFolder
        }
        //对图片进行文件夹分类
        if (imageFileList != null && imageFileList.isNotEmpty()) {
            val size = imageFileList.size
            //添加其他的图片文件夹
            for (i in 0 until size) {
                val mediaFile = imageFileList[i]
                val imageFolderId = mediaFile.folderId!!
                var mediaFolder = mediaFolderMap[imageFolderId]
                if (mediaFolder == null) {
                    mediaFolder = MediaFolder(imageFolderId, mediaFile.folderName!!, mediaFile.path, ArrayList())
                }
                val imageList = mediaFolder.mediaFileList
                imageList.add(mediaFile)
                mediaFolder.mediaFileList = imageList
                mediaFolderMap[imageFolderId] = mediaFolder
            }
        }
        //整理聚类数据
        val mediaFolderList: MutableList<MediaFolder> = ArrayList()
        for (folderId in mediaFolderMap.keys) {
            mediaFolderList.add(mediaFolderMap[folderId]!!)
        }
        //按照图片文件夹的数量排序
        mediaFolderList.sortWith { o1, o2 ->
            when {
                o1!!.mediaFileList.size > o2!!.mediaFileList.size -> -1
                o1.mediaFileList.size < o2.mediaFileList.size -> 1
                else -> 0
            }
        }
        return mediaFolderList
    }

}
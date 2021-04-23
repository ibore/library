package me.ibore.image.picker

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import me.ibore.image.picker.activity.ImagePickerActivity

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ImagePicker private constructor(private var config: Config = Config()) {

    companion object {

        const val EXTRA_SELECT_IMAGES = "selectItems"

        private var mImagePicker: ImagePicker? = null

        fun start(): ImagePicker {
            if (mImagePicker == null) {
                synchronized(ImagePicker::class.java) {
                    if (mImagePicker == null) {
                        mImagePicker = ImagePicker()
                    }
                }
            }
            return mImagePicker!!
        }

        fun start(config: Config): ImagePicker {
            val imagePicker = start()
            imagePicker.config = config
            return imagePicker
        }

        fun getConfig(): Config {
            return mImagePicker!!.config
        }

        fun onActivityResult(data: Intent?): ArrayList<String> {
            return data?.getStringArrayListExtra(EXTRA_SELECT_IMAGES) ?: arrayListOf()
        }
    }

    // 是否支持相机
    fun showCamera(): ImagePicker {
        return showCamera(true)
    }

    // 是否支持相机
    fun showCamera(showCamera: Boolean): ImagePicker {
        config.showCamera = showCamera
        return this
    }

    // 是否展示图片
    fun showImage(): ImagePicker {
        return showImage(true)
    }

    // 是否展示图片
    fun showImage(showImage: Boolean): ImagePicker {
        config.showImage = showImage
        return this
    }

    // 是否展示图片
    fun oneCount(): ImagePicker {
        config.maxCount = 1
        return this
    }

    // 是否展示视频
    fun showVideo(): ImagePicker {
        return showVideo(true)
    }

    // 是否展示视频
    fun showVideo(showVideo: Boolean): ImagePicker {
        config.showVideo = showVideo
        return this
    }

    // 是否过滤GIF图片(默认不过滤)
    fun filterGif(): ImagePicker {
        return filterGif(true)
    }

    // 是否过滤GIF图片(默认不过滤)
    fun filterGif(filterGif: Boolean): ImagePicker {
        config.filterGif = filterGif
        return this
    }

    // 图片最大选择数
    fun maxCount(maxCount: Int): ImagePicker {
        if (config.showCamera) {
            return this
        }
        config.maxCount = maxCount
        return this
    }

    // 图片压缩
    fun imageCompress(): ImagePicker {
        config.imageQuality = 1
        return this
    }

    // 压缩图片
    fun imageOriginal(): ImagePicker {
        config.imageQuality = 2
        return this
    }

    // 图片质量用户选择，默认压缩图片
    fun imageQualityUser(): ImagePicker {
        config.imageQuality = 3
        return this
    }

    // 设置单类型选择（只能选图片或者视频）
    fun singleType(): ImagePicker {
        return singleType(true)
    }

    // 设置单类型选择（只能选图片或者视频）
    fun singleType(singleType: Boolean): ImagePicker {
        config.singleType = singleType
        return this
    }

    // 设置图片选择历史记录
    fun imagePaths(imagePaths: ArrayList<String>?): ImagePicker {
        config.imagePaths = imagePaths
        return this
    }

    // 启动
    fun request(activity: Activity, requestCode: Int) {
        val intent = Intent(activity, ImagePickerActivity::class.java)
        activity.startActivityForResult(intent, requestCode)
    }

    // 启动
    fun request(fragment: Fragment, requestCode: Int) {
        val intent = Intent(fragment.activity, ImagePickerActivity::class.java)
        fragment.startActivityForResult(intent, requestCode)
    }

    data class Config(
            //是否显示拍照Item，默认显示
            var showCamera: Boolean = false,
            //是否显示图片，默认显示
            var showImage: Boolean = true,
            //是否显示视频，默认不显示
            var showVideo: Boolean = false,
            //是否过滤GIF图片，默认不过滤
            var filterGif: Boolean = false,
            //最大选择数量，默认为1
            var maxCount: Int = 1,
            //图片质量（1压缩，2原图，3用户选择压缩，4用户选择原图），默认为1
            var imageQuality: Int = 1,
            //是否只支持选单类型（图片或者视频）
            var singleType: Boolean = false,
            //上一次选择的图片地址集合
            var imagePaths: ArrayList<String>? = null)
}
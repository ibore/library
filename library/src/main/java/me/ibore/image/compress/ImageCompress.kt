package me.ibore.image.compress

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.annotation.IntDef
import androidx.exifinterface.media.ExifInterface
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import me.ibore.base.XObserver
import me.ibore.base.XSubscriber
import me.ibore.utils.DisposablesUtils
import me.ibore.utils.Utils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.annotation.Inherited
import kotlin.math.ceil
import kotlin.math.sqrt


object ImageCompress {

    @IntDef(GEAR.HIGH, GEAR.LOW, GEAR.CUSTOM)
    @Target(AnnotationTarget.VALUE_PARAMETER)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @MustBeDocumented
    @Inherited
    annotation class GEAR {
        companion object {
            const val HIGH = 1
            const val LOW = 2
            const val CUSTOM = 3
        }
    }

    fun compress(file: File): CompressSingle {
        return CompressSingle(file, getDefaultCacheDir())
    }

    fun compress(files: MutableList<File>): CompressMulti {
        return CompressMulti(files, getDefaultCacheDir())
    }

    // file 要压缩的单个文件 cacheDir 压缩完文件的存储路径
    fun compress(file: File, cacheDir: File): CompressSingle {
        require(isCacheDirValid(cacheDir)) { "The cacheDir must be Directory" }
        return CompressSingle(file, cacheDir)
    }

    fun compress(files: MutableList<File>, cacheDir: File): CompressMulti {
        require(isCacheDirValid(cacheDir)) { "The cacheDir must be Directory" }
        return CompressMulti(files, cacheDir)
    }

    private fun isCacheDirValid(cacheDir: File): Boolean {
        return cacheDir.isDirectory && (cacheDir.exists() || cacheDir.mkdirs())
    }

    private fun getDefaultCacheDir(): File {
        val cacheDir = File(Utils.app.cacheDir, "image_compress_cache")
        cacheDir.mkdirs()
        return cacheDir
    }

    class CompressSingle(private val file: File, cacheDir: File) : Compress<CompressSingle>(cacheDir) {

        fun observable(tag: Any?, observer: XObserver<File>): Disposable {
            return DisposablesUtils.add(tag ?: observer.tag!!, Observable.create {
                try {
                    it.onNext(get())
                    it.onComplete()
                } catch (e: Exception) {
                    if (!observer.isDisposed) it.onError(e)
                }
            }, observer)
        }

        fun subscriber(tag: Any?, subscriber: XSubscriber<File>): Disposable {
            return DisposablesUtils.add(tag ?: subscriber.tag!!, Flowable.create({
                try {
                    it.onNext(get())
                    it.onComplete()
                } catch (e: Exception) {
                    if (!subscriber.isDisposed) it.onError(e)
                }
            }, BackpressureStrategy.DROP), subscriber)
        }

        fun get(): File {
            return compressImage(file)
        }

    }

    class CompressMulti(private val files: MutableList<File>, cacheDir: File) : Compress<CompressMulti>(cacheDir) {

        fun observable(tag: Any?, observer: XObserver<MutableList<File>>): Disposable {
            return DisposablesUtils.add(tag ?: observer.tag!!, Observable.create {
                try {
                    it.onNext(get())
                    it.onComplete()
                } catch (e: Exception) {
                    if (!observer.isDisposed) it.onError(e)
                }
            }, observer)
        }

        fun subscriber(tag: Any?, subscriber: XSubscriber<MutableList<File>>): Disposable {
            return DisposablesUtils.add(tag ?: subscriber.tag!!, Flowable.create({
                try {
                    it.onNext(get())
                    it.onComplete()
                } catch (e: Exception) {
                    if (!subscriber.isDisposed) it.onError(e)
                }
            }, BackpressureStrategy.DROP), subscriber)
        }

        fun get(): MutableList<File> {
            val compressFiles = ArrayList<File>(files.size)
            for (file in files) {
                compressFiles.add(compressImage(file))
            }
            return compressFiles
        }

    }


    @Suppress("UNCHECKED_CAST")
    open class Compress<T : Compress<T>>(private val cacheDir: File) {
        private var maxSize = 0
        private var maxWidth = 0
        private var maxHeight = 0
        private var compressFormat = Bitmap.CompressFormat.JPEG
        private var gear: Int = GEAR.LOW
        private var mByteArrayOutputStream: ByteArrayOutputStream? = null


        /**
         * 自定义压缩模式 LOW、HIGH、CUSTOM
         */
        fun gear(@GEAR gear: Int): T {
            this.gear = gear
            return this as T
        }

        /**
         * 自定义图片压缩格式
         */
        fun compressFormat(compressFormat: Bitmap.CompressFormat?): T {
            this.compressFormat = compressFormat!!
            return this as T
        }

        /**
         * CUSTOM_GEAR 指定目标图片的最大体积
         */
        fun maxSize(size: Int): T {
            this.maxSize = size
            return this as T
        }

        /**
         * CUSTOM_GEAR 指定目标图片的最大宽度
         *
         * @param width 最大宽度
         */
        fun maxWidth(width: Int): T {
            this.maxWidth = width
            return this as T
        }

        /**
         * CUSTOM_GEAR 指定目标图片的最大高度
         *
         * @param height 最大高度
         */
        fun maxHeight(height: Int): T {
            this.maxHeight = height
            return this as T
        }

        /**
         * 清空Luban所产生的缓存
         * Clears the cache generated by Luban
         */
        fun clearCache(): T {
            if (cacheDir.exists()) {
                deleteFile(cacheDir)
            }
            return this as T
        }

        /**
         * 清空目标文件或文件夹
         * Empty the target file or folder
         */
        private fun deleteFile(fileOrDirectory: File) {
            if (fileOrDirectory.isDirectory) {
                val files = fileOrDirectory.listFiles()
                if (!files.isNullOrEmpty()) {
                    for (file in files) {
                        deleteFile(file)
                    }
                }
            }
            fileOrDirectory.delete()
        }


        @Throws(IOException::class)
        fun compressImage(file: File): File {
            return when (gear) {
                GEAR.LOW -> lowCompress(file)
                GEAR.HIGH -> highCompress(file)
                GEAR.CUSTOM -> customCompress(file)
                else -> file
            }
        }

        @Throws(IOException::class)
        private fun lowCompress(file: File): File {
            val thumb = cacheFilePath
            var size: Double
            val filePath = file.absolutePath
            val angle = getImageSpinAngle(filePath)
            var width = getImageSize(filePath)[0]
            var height = getImageSize(filePath)[1]
            val flip = width > height
            var thumbW = if (width % 2 == 1) width + 1 else width
            var thumbH = if (height % 2 == 1) height + 1 else height
            width = if (thumbW > thumbH) thumbH else thumbW
            height = if (thumbW > thumbH) thumbW else thumbH
            val scale = width.toDouble() / height
            if (scale <= 1 && scale > 0.5625) {
                when {
                    height < 1664 -> {
                        if (file.length() / 1024 < 150) {
                            return file
                        }
                        size = width * height / Math.pow(1664.0, 2.0) * 150
                        size = if (size < 60) 60.0 else size
                    }
                    height in 1664..4989 -> {
                        thumbW = width / 2
                        thumbH = height / 2
                        size = thumbW * thumbH / Math.pow(2495.0, 2.0) * 300
                        size = if (size < 60) 60.0 else size
                    }
                    height in 4990..10239 -> {
                        thumbW = width / 4
                        thumbH = height / 4
                        size = thumbW * thumbH / Math.pow(2560.0, 2.0) * 300
                        size = if (size < 100) 100.0 else size
                    }
                    else -> {
                        val multiple = if (height / 1280 == 0) 1 else height / 1280
                        thumbW = width / multiple
                        thumbH = height / multiple
                        size = thumbW * thumbH / Math.pow(2560.0, 2.0) * 300
                        size = if (size < 100) 100.0 else size
                    }
                }
            } else if (scale <= 0.5625 && scale > 0.5) {
                if (height < 1280 && file.length() / 1024 < 200) {
                    return file
                }
                val multiple = if (height / 1280 == 0) 1 else height / 1280
                thumbW = width / multiple
                thumbH = height / multiple
                size = thumbW * thumbH / (1440.0 * 2560.0) * 400
                size = if (size < 100) 100.0 else size
            } else {
                val multiple = ceil(height / (1280.0 / scale)).toInt()
                thumbW = width / multiple
                thumbH = height / multiple
                size = thumbW * thumbH / (1280.0 * (1280 / scale)) * 500
                size = if (size < 100) 100.0 else size
            }
            return compress(filePath, thumb, if (flip) thumbH else thumbW, if (flip) thumbW else thumbH, angle,
                    size.toLong())
        }

        @Throws(IOException::class)
        private fun highCompress(file: File): File {
            val minSize = 60
            val longSide = 720
            val shortSide = 1280
            val thumbFilePath = cacheFilePath
            val filePath = file.absolutePath
            var size: Long = 0
            val maxSize = file.length() / 5
            val angle = getImageSpinAngle(filePath)
            val imgSize = getImageSize(filePath)
            var width = 0
            var height = 0
            if (imgSize[0] <= imgSize[1]) {
                val scale = imgSize[0].toDouble() / imgSize[1].toDouble()
                if (scale <= 1.0 && scale > 0.5625) {
                    width = if (imgSize[0] > shortSide) shortSide else imgSize[0]
                    height = width * imgSize[1] / imgSize[0]
                    size = minSize.toLong()
                } else if (scale <= 0.5625) {
                    height = if (imgSize[1] > longSide) longSide else imgSize[1]
                    width = height * imgSize[0] / imgSize[1]
                    size = maxSize
                }
            } else {
                val scale = imgSize[1].toDouble() / imgSize[0].toDouble()
                if (scale <= 1.0 && scale > 0.5625) {
                    height = if (imgSize[1] > shortSide) shortSide else imgSize[1]
                    width = height * imgSize[0] / imgSize[1]
                    size = minSize.toLong()
                } else if (scale <= 0.5625) {
                    width = if (imgSize[0] > longSide) longSide else imgSize[0]
                    height = width * imgSize[1] / imgSize[0]
                    size = maxSize
                }
            }
            return compress(filePath, thumbFilePath, width, height, angle, size)
        }

        @Throws(IOException::class)
        private fun customCompress(file: File): File {
            val thumbFilePath = cacheFilePath
            val filePath = file.absolutePath
            val angle = getImageSpinAngle(filePath)
            val fileSize = if (maxSize > 0 && maxSize < file.length() / 1024) maxSize.toLong() else file.length() / 1024
            val size = getImageSize(filePath)
            var width = size[0]
            var height = size[1]
            if (maxSize > 0 && maxSize < file.length() / 1024f) {
                // find a suitable size
                val scale = sqrt(file.length() / 1024f / maxSize.toDouble()).toFloat()
                width = (width / scale).toInt()
                height = (height / scale).toInt()
            }

            // check the width&height
            if (maxWidth > 0) {
                width = width.coerceAtMost(maxWidth)
            }
            if (maxHeight > 0) {
                height = height.coerceAtMost(maxHeight)
            }
            val scale = (width.toFloat() / size[0]).coerceAtMost(height.toFloat() / size[1])
            width = (size[0] * scale).toInt()
            height = (size[1] * scale).toInt()

            // 不压缩
            return if (maxSize > file.length() / 1024f && scale == 1f) {
                file
            } else compress(filePath, thumbFilePath, width, height, angle, fileSize)
        }

        private val cacheFilePath: String
            get() {
                val name = StringBuilder("image_" + System.currentTimeMillis())
                if (compressFormat == Bitmap.CompressFormat.WEBP) {
                    name.append(".webp")
                } else {
                    name.append(".jpg")
                }
                return cacheDir.absolutePath + File.separator + name
            }

        /**
         * obtain the thumbnail that specify the size
         *
         * @param imagePath the target image path
         * @param width     the width of thumbnail
         * @param height    the height of thumbnail
         * @return [Bitmap]
         */
        private fun compress(imagePath: String, width: Int, height: Int): Bitmap {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, options)
            val outH = options.outHeight
            val outW = options.outWidth
            var inSampleSize = 1
            while (outH / inSampleSize > height || outW / inSampleSize > width) {
                inSampleSize *= 2
            }
            options.inSampleSize = inSampleSize
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(imagePath, options)
        }

        /**
         * obtain the image rotation angle
         *
         * @param path path of target image
         */
        private fun getImageSpinAngle(path: String): Int {
            var degree = 0
            var exifInterface: ExifInterface? = null
            exifInterface = try {
                ExifInterface(path)
            } catch (e: IOException) {
                // 图片不支持获取角度
                return 0
            }
            val orientation = exifInterface?.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
            return degree
        }

        /**
         * 指定参数压缩图片
         * create the thumbnail with the true rotate angle
         *
         * @param largeImagePath the big image path
         * @param thumbFilePath  the thumbnail path
         * @param width          width of thumbnail
         * @param height         height of thumbnail
         * @param angle          rotation angle of thumbnail
         * @param size           the file size of image
         */
        @Throws(IOException::class)
        private fun compress(largeImagePath: String, thumbFilePath: String, width: Int, height: Int,
                             angle: Int, size: Long): File {
            var thbBitmap = compress(largeImagePath, width, height)
            thbBitmap = rotatingImage(angle, thbBitmap)
            return saveImage(thumbFilePath, thbBitmap, size)
        }

        /**
         * 保存图片到指定路径
         * Save image with specified size
         *
         * @param filePath the image file save path 储存路径
         * @param bitmap   the image what be save   目标图片
         * @param size     the file size of image   期望大小
         */
        @Throws(IOException::class)
        private fun saveImage(filePath: String, bitmap: Bitmap, size: Long): File {
            /*val result = File(filePath.substring(0, filePath.lastIndexOf("/")))
            if (!result.exists() && !result.mkdirs()) {
                return null
            }*/
            if (mByteArrayOutputStream == null) {
                mByteArrayOutputStream = ByteArrayOutputStream(
                        bitmap.width * bitmap.height)
            } else {
                mByteArrayOutputStream!!.reset()
            }
            var options = 100
            bitmap.compress(compressFormat, options, mByteArrayOutputStream)
            while (mByteArrayOutputStream!!.size() / 1024 > size && options > 6) {
                mByteArrayOutputStream!!.reset()
                options -= 6
                bitmap.compress(compressFormat, options, mByteArrayOutputStream)
            }
            bitmap.recycle()
            val fos = FileOutputStream(filePath)
            mByteArrayOutputStream!!.writeTo(fos)
            fos.close()
            return File(filePath)
        }


        /**
         * obtain the image's width and height
         *
         * @param imagePath the path of image
         */
        fun getImageSize(imagePath: String?): IntArray {
            val res = IntArray(2)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            options.inSampleSize = 1
            BitmapFactory.decodeFile(imagePath, options)
            res[0] = options.outWidth
            res[1] = options.outHeight
            return res
        }

        /**
         * 旋转图片
         * rotate the image with specified angle
         *
         * @param angle  the angle will be rotating 旋转的角度
         * @param bitmap target image               目标图片
         */
        private fun rotatingImage(angle: Int, bitmap: Bitmap): Bitmap {
            //rotate image
            val matrix = Matrix()
            matrix.postRotate(angle.toFloat())
            //create a new image
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }


}
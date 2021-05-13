package me.ibore.qrcode

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.hardware.Camera
import android.hardware.Camera.PreviewCallback
import android.os.AsyncTask
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.RelativeLayout
import me.ibore.R
import me.ibore.utils.BarUtils
import kotlin.math.abs
import kotlin.math.sqrt

abstract class QRCodeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RelativeLayout(context, attrs, defStyleAttr), PreviewCallback {

    protected var mCamera: Camera? = null
    protected var cameraPreview = CameraPreview(context)
    protected var scanBoxView: ScanBoxView
    protected var mDelegate: Delegate? = null
    protected var mSpotAble = false
    protected var mProcessDataTask: ProcessDataTask? = null
    protected var mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK
    private var mLocationPoints: Array<PointF?>? = null
    private var mPaint: Paint? = null
    protected var mBarcodeType: BarcodeType = BarcodeType.HIGH_FREQUENCY
    private var mLastPreviewFrameTime: Long = 0
    private var mAutoZoomAnimator: ValueAnimator? = null
    private var mLastAutoZoomTime: Long = 0

    // 上次环境亮度记录的时间戳
    private var mLastAmbientBrightnessRecordTime = System.currentTimeMillis()

    // 上次环境亮度记录的索引
    private var mAmbientBrightnessDarkIndex = 0

    init {
        cameraPreview.setDelegate(object : CameraPreview.Delegate {
            override fun onStartPreview() {
                setOneShotPreviewCallback()
            }
        })
        scanBoxView = ScanBoxView(context)
        scanBoxView.init(this, attrs)
        cameraPreview.id = R.id.bgaqrcode_camera_preview
        addView(cameraPreview)
        val layoutParams = LayoutParams(context, attrs)
        layoutParams.addRule(ALIGN_TOP, cameraPreview.id)
        layoutParams.addRule(ALIGN_BOTTOM, cameraPreview.id)
        addView(scanBoxView, layoutParams)
        mPaint = Paint()
        mPaint!!.color = scanBoxView.cornerColor
        mPaint!!.style = Paint.Style.FILL

        setupReader()
    }

    private fun setOneShotPreviewCallback() {
        if (mSpotAble && cameraPreview.isPreviewing) {
            try {
                mCamera!!.setOneShotPreviewCallback(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    protected abstract fun setupReader()

    /**
     * 设置扫描二维码的代理
     *
     * @param delegate 扫描二维码的代理
     */
    fun setDelegate(delegate: Delegate?) {
        mDelegate = delegate
    }
    /**
     * 显示扫描框
     */
    fun showScanRect() {
        scanBoxView.visibility = VISIBLE
    }

    /**
     * 隐藏扫描框
     */
    fun hiddenScanRect() {
        scanBoxView.visibility = GONE
    }
    /**
     * 打开指定摄像头开始预览，但是并未开始识别
     */
    /**
     * 打开后置摄像头开始预览，但是并未开始识别
     */
    @JvmOverloads
    fun startCamera(cameraFacing: Int = mCameraId) {
        if (mCamera != null || Camera.getNumberOfCameras() == 0) {
            return
        }
        var ultimateCameraId = findCameraIdByFacing(cameraFacing)
        if (ultimateCameraId != NO_CAMERA_ID) {
            startCameraById(ultimateCameraId)
            return
        }
        if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            ultimateCameraId = findCameraIdByFacing(Camera.CameraInfo.CAMERA_FACING_FRONT)
        } else if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            ultimateCameraId = findCameraIdByFacing(Camera.CameraInfo.CAMERA_FACING_BACK)
        }
        if (ultimateCameraId != NO_CAMERA_ID) {
            startCameraById(ultimateCameraId)
        }
    }

    private fun findCameraIdByFacing(cameraFacing: Int): Int {
        val cameraInfo = Camera.CameraInfo()
        for (cameraId in 0 until Camera.getNumberOfCameras()) {
            try {
                Camera.getCameraInfo(cameraId, cameraInfo)
                if (cameraInfo.facing == cameraFacing) {
                    return cameraId
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return NO_CAMERA_ID
    }

    private fun startCameraById(cameraId: Int) {
        try {
            mCameraId = cameraId
            mCamera = Camera.open(cameraId)
            cameraPreview.setCamera(mCamera)
        } catch (e: Exception) {
            e.printStackTrace()
            if (mDelegate != null) {
                mDelegate!!.onScanQRCodeOpenCameraError()
            }
        }
    }

    /**
     * 关闭摄像头预览，并且隐藏扫描框
     */
    fun stopCamera() {
        try {
            stopSpotAndHiddenRect()
            if (mCamera != null) {
                cameraPreview.stopCameraPreview()
                cameraPreview.setCamera(null)
                mCamera!!.release()
                mCamera = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 开始识别
     */
    fun startSpot() {
        mSpotAble = true
        startCamera()
        setOneShotPreviewCallback()
    }

    /**
     * 停止识别
     */
    fun stopSpot() {
        mSpotAble = false
        if (mProcessDataTask != null) {
            mProcessDataTask!!.cancelTask()
            mProcessDataTask = null
        }
        if (mCamera != null) {
            try {
                mCamera!!.setOneShotPreviewCallback(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 停止识别，并且隐藏扫描框
     */
    fun stopSpotAndHiddenRect() {
        stopSpot()
        hiddenScanRect()
    }

    /**
     * 显示扫描框，并开始识别
     */
    fun startSpotAndShowRect() {
        startSpot()
        showScanRect()
    }

    /**
     * 打开闪光灯
     */
    fun openFlashlight() {
        postDelayed({ cameraPreview.openFlashlight() }, if (cameraPreview.isPreviewing) 0 else 500.toLong())
    }

    /**
     * 关闭闪光灯
     */
    fun closeFlashlight() {
        cameraPreview.closeFlashlight()
    }

    /**
     * 销毁二维码扫描控件
     */
    fun onDestroy() {
        stopCamera()
        mDelegate = null
    }

    /**
     * 切换成扫描条码样式
     */
    fun changeToScanBarcodeStyle() {
        if (!scanBoxView.isBarcode) {
            scanBoxView.isBarcode = true
        }
    }

    /**
     * 切换成扫描二维码样式
     */
    fun changeToScanQRCodeStyle() {
        if (scanBoxView.isBarcode) {
            scanBoxView.isBarcode = false
        }
    }

    /**
     * 当前是否为条码扫描样式
     */
    val isScanBarcodeStyle: Boolean
        get() = scanBoxView.isBarcode

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        if (BGAQRCodeUtil.isDebug()) {
            BGAQRCodeUtil.d("两次 onPreviewFrame 时间间隔：" + (System.currentTimeMillis() - mLastPreviewFrameTime))
            mLastPreviewFrameTime = System.currentTimeMillis()
        }
        if (cameraPreview.isPreviewing) {
            try {
                handleAmbientBrightness(data, camera)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (!mSpotAble || mProcessDataTask != null && (mProcessDataTask!!.status == AsyncTask.Status.PENDING
                        || mProcessDataTask!!.status == AsyncTask.Status.RUNNING)) {
            return
        }
        mProcessDataTask = ProcessDataTask(camera, data, this, BGAQRCodeUtil.isPortrait(context)).perform()
    }

    private fun handleAmbientBrightness(data: ByteArray, camera: Camera) {
        if (!cameraPreview.isPreviewing) {
            return
        }
        val currentTime = System.currentTimeMillis()
        if (currentTime - mLastAmbientBrightnessRecordTime < AMBIENT_BRIGHTNESS_WAIT_SCAN_TIME) {
            return
        }
        mLastAmbientBrightnessRecordTime = currentTime
        val width = camera.parameters.previewSize.width
        val height = camera.parameters.previewSize.height
        // 像素点的总亮度
        var pixelLightCount = 0L
        // 像素点的总数
        val pixelCount = width * height.toLong()
        // 采集步长，因为没有必要每个像素点都采集，可以跨一段采集一个，减少计算负担，必须大于等于1。
        val step = 10
        // data.length - allCount * 1.5f 的目的是判断图像格式是不是 YUV420 格式，只有是这种格式才相等
        //因为 int 整形与 float 浮点直接比较会出问题，所以这么比
        if (Math.abs(data.size - pixelCount * 1.5f) < 0.00001f) {
            var i = 0
            while (i < pixelCount) {

                // 如果直接加是不行的，因为 data[i] 记录的是色值并不是数值，byte 的范围是 +127 到 —128，
                // 而亮度 FFFFFF 是 11111111 是 -127，所以这里需要先转为无符号 unsigned long 参考 Byte.toUnsignedLong()
                pixelLightCount += data[i].toLong() and 0xffL
                i += step
            }
            // 平均亮度
            val cameraLight = pixelLightCount / (pixelCount / step)
            // 更新历史记录
            val lightSize = AMBIENT_BRIGHTNESS_DARK_LIST.size
            AMBIENT_BRIGHTNESS_DARK_LIST[mAmbientBrightnessDarkIndex % lightSize.also { mAmbientBrightnessDarkIndex = it }] = cameraLight
            mAmbientBrightnessDarkIndex++
            var isDarkEnv = true
            // 判断在时间范围 AMBIENT_BRIGHTNESS_WAIT_SCAN_TIME * lightSize 内是不是亮度过暗
            for (ambientBrightness in AMBIENT_BRIGHTNESS_DARK_LIST) {
                if (ambientBrightness > AMBIENT_BRIGHTNESS_DARK) {
                    isDarkEnv = false
                    break
                }
            }
            BGAQRCodeUtil.d("摄像头环境亮度为：$cameraLight")
            if (mDelegate != null) {
                mDelegate!!.onCameraAmbientBrightnessChanged(isDarkEnv)
            }
        }
    }

    /**
     * 解析本地图片二维码。返回二维码图片里的内容 或 null
     *
     * @param picturePath 要解析的二维码图片本地路径
     */
    fun decodeQRCode(picturePath: String?) {
        mProcessDataTask = ProcessDataTask(picturePath, this).perform()
    }

    /**
     * 解析 Bitmap 二维码。返回二维码图片里的内容 或 null
     *
     * @param bitmap 要解析的二维码图片
     */
    fun decodeQRCode(bitmap: Bitmap?) {
        mProcessDataTask = ProcessDataTask(bitmap, this).perform()
    }

    abstract fun processData(data: ByteArray?, width: Int, height: Int, isRetry: Boolean): ScanResult?

    abstract fun processBitmapData(bitmap: Bitmap?): ScanResult?

    fun onPostParseData(scanResult: ScanResult?) {
        if (!mSpotAble) {
            return
        }
        val result: String? = scanResult?.result
        if (TextUtils.isEmpty(result)) {
            try {
                if (mCamera != null) {
                    mCamera!!.setOneShotPreviewCallback(this@QRCodeView)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            mSpotAble = false
            try {
                if (mDelegate != null) {
                    mDelegate!!.onScanQRCodeSuccess(result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onPostParseBitmapOrPicture(scanResult: ScanResult?) {
        if (mDelegate != null) {
            val result: String? = scanResult?.result
            mDelegate!!.onScanQRCodeSuccess(result)
        }
    }

    fun onScanBoxRectChanged(rect: Rect?) {
        cameraPreview.onScanBoxRectChanged(rect)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (!isShowLocationPoint || mLocationPoints == null) {
            return
        }
        for (pointF in mLocationPoints!!) {
            canvas.drawCircle(pointF!!.x, pointF.y, 10f, mPaint!!)
        }
        mLocationPoints = null
        postInvalidateDelayed(2000)
    }

    /**
     * 是否显示定位点
     */
    protected val isShowLocationPoint: Boolean
        get() = scanBoxView.isShowLocationPoint

    /**
     * 是否自动缩放
     */
    protected val isAutoZoom: Boolean
        get() = scanBoxView?.isAutoZoom ?: false

    protected fun transformToViewCoordinates(pointArr: Array<PointF>?, scanBoxAreaRect: Rect?, isNeedAutoZoom: Boolean, result: String): Boolean {
        return if (pointArr.isNullOrEmpty()) {
            false
        } else try {
            // 不管横屏还是竖屏，size.width 大于 size.height
            val size = mCamera!!.parameters.previewSize
            val isMirrorPreview = mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT
            val statusBarHeight: Int = BarUtils.getStatusBarHeight(context)
            val transformedPoints = arrayOfNulls<PointF>(pointArr.size)
            for ((index, qrPoint) in pointArr.withIndex()) {
                transformedPoints[index] = transform(qrPoint.x, qrPoint.y, size.width.toFloat(), size.height.toFloat(), isMirrorPreview, statusBarHeight, scanBoxAreaRect)
            }
            mLocationPoints = transformedPoints
            postInvalidate()
            if (isNeedAutoZoom) {
                handleAutoZoom(transformedPoints, result)
            } else false
        } catch (e: Exception) {
            mLocationPoints = null
            e.printStackTrace()
            false
        }
    }

    private fun handleAutoZoom(locationPoints: Array<PointF?>?, result: String): Boolean {
        if (mCamera == null) {
            return false
        }
        if (locationPoints.isNullOrEmpty()) {
            return false
        }
        if (mAutoZoomAnimator != null && mAutoZoomAnimator!!.isRunning) {
            return true
        }
        if (System.currentTimeMillis() - mLastAutoZoomTime < 1200) {
            return true
        }
        val parameters = mCamera!!.parameters
        if (!parameters.isZoomSupported) {
            return false
        }
        val point1X = locationPoints[0]!!.x
        val point1Y = locationPoints[0]!!.y
        val point2X = locationPoints[1]!!.x
        val point2Y = locationPoints[1]!!.y
        val xLen = abs(point1X - point2X)
        val yLen = abs(point1Y - point2Y)
        val len = sqrt(xLen * xLen + yLen * yLen.toDouble()).toInt()
        val scanBoxWidth: Int = scanBoxView!!.rectWidth
        if (len > scanBoxWidth / 4) {
            return false
        }
        // 二维码在扫描框中的宽度小于扫描框的 1/4，放大镜头
        val maxZoom = parameters.maxZoom
        val zoomStep = maxZoom / 4
        val zoom = parameters.zoom
        post { startAutoZoom(zoom, (zoom + zoomStep).coerceAtMost(maxZoom), result) }
        return true
    }

    private fun startAutoZoom(oldZoom: Int, newZoom: Int, result: String) {
        mAutoZoomAnimator = ValueAnimator.ofInt(oldZoom, newZoom)
        mAutoZoomAnimator!!.addUpdateListener(AnimatorUpdateListener { animation ->
            if (!cameraPreview.isPreviewing) {
                return@AnimatorUpdateListener
            }
            val zoom = animation.animatedValue as Int
            val parameters = mCamera!!.parameters
            parameters.zoom = zoom
            mCamera!!.parameters = parameters
        })
        mAutoZoomAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onPostParseData(ScanResult(result))
            }
        })
        mAutoZoomAnimator!!.duration = 600
        mAutoZoomAnimator!!.repeatCount = 0
        mAutoZoomAnimator!!.start()
        mLastAutoZoomTime = System.currentTimeMillis()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mAutoZoomAnimator != null) {
            mAutoZoomAnimator!!.cancel()
        }
    }

    private fun transform(originX: Float, originY: Float, cameraPreviewWidth: Float, cameraPreviewHeight: Float, isMirrorPreview: Boolean, statusBarHeight: Int,
                          scanBoxAreaRect: Rect?): PointF {
        val viewWidth = width
        val viewHeight = height
        val result: PointF
        val scaleX: Float
        val scaleY: Float
        if (BGAQRCodeUtil.isPortrait(context)) {
            scaleX = viewWidth / cameraPreviewHeight
            scaleY = viewHeight / cameraPreviewWidth
            result = PointF((cameraPreviewHeight - originX) * scaleX, (cameraPreviewWidth - originY) * scaleY)
            result.y = viewHeight - result.y
            result.x = viewWidth - result.x
            if (scanBoxAreaRect == null) {
                result.y += statusBarHeight.toFloat()
            }
        } else {
            scaleX = viewWidth / cameraPreviewWidth
            scaleY = viewHeight / cameraPreviewHeight
            result = PointF(originX * scaleX, originY * scaleY)
            if (isMirrorPreview) {
                result.x = viewWidth - result.x
            }
        }
        if (scanBoxAreaRect != null) {
            result.y += scanBoxAreaRect.top.toFloat()
            result.x += scanBoxAreaRect.left.toFloat()
        }
        return result
    }

    interface Delegate {
        /**
         * 处理扫描结果
         *
         * @param result 摄像头扫码时只要回调了该方法 result 就一定有值，不会为 null。解析本地图片或 Bitmap 时 result 可能为 null
         */
        fun onScanQRCodeSuccess(result: String?)

        /**
         * 摄像头环境亮度发生变化
         *
         * @param isDark 是否变暗
         */
        fun onCameraAmbientBrightnessChanged(isDark: Boolean)

        /**
         * 处理打开相机出错
         */
        fun onScanQRCodeOpenCameraError()
    }

    companion object {
        private const val NO_CAMERA_ID = -1

        // 环境亮度历史记录的数组，255 是代表亮度最大值
        private val AMBIENT_BRIGHTNESS_DARK_LIST = longArrayOf(255, 255, 255, 255)

        // 环境亮度扫描间隔
        private const val AMBIENT_BRIGHTNESS_WAIT_SCAN_TIME = 150

        // 亮度低的阀值
        private const val AMBIENT_BRIGHTNESS_DARK = 60
    }

}
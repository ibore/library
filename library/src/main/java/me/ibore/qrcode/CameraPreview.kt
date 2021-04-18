package me.ibore.qrcode

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.Rect
import android.hardware.Camera
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import me.ibore.ktx.dp2px
import me.ibore.utils.LogUtils

class CameraPreview(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private var mCamera: Camera? = null
    private var mPreviewing = false
    private var mSurfaceCreated = false
    private var mIsTouchFocusing = false
    private var mOldDist = 1f
    private var mCameraConfigurationManager: CameraConfigurationManager? = null
    private var mDelegate: Delegate? = null
    fun setCamera(camera: Camera?) {
        mCamera = camera
        if (mCamera != null) {
            mCameraConfigurationManager = CameraConfigurationManager(context)
            mCameraConfigurationManager!!.initFromCameraParameters(mCamera!!)
            if (mPreviewing) {
                requestLayout()
            } else {
                showCameraPreview()
            }
        }
    }

    fun setDelegate(delegate: Delegate) {
        mDelegate = delegate
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        mSurfaceCreated = true
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (surfaceHolder.surface == null) {
            return
        }
        stopCameraPreview()
        showCameraPreview()
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        mSurfaceCreated = false
        stopCameraPreview()
    }

    fun reactNativeShowCameraPreview() {
        if (holder == null || holder.surface == null) {
            return
        }
        stopCameraPreview()
        showCameraPreview()
    }

    private fun showCameraPreview() {
        if (mCamera != null) {
            try {
                mPreviewing = true
                val surfaceHolder = holder
                surfaceHolder.setKeepScreenOn(true)
                mCamera!!.setPreviewDisplay(surfaceHolder)
                mCameraConfigurationManager!!.setDesiredCameraParameters(mCamera!!)
                mCamera!!.startPreview()
                if (mDelegate != null) {
                    mDelegate!!.onStartPreview()
                }
                startContinuousAutoFocus()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopCameraPreview() {
        if (mCamera != null) {
            try {
                mPreviewing = false
                mCamera!!.cancelAutoFocus()
                mCamera!!.setOneShotPreviewCallback(null)
                mCamera!!.stopPreview()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun openFlashlight() {
        if (flashLightAvailable()) {
            mCameraConfigurationManager!!.openFlashlight(mCamera!!)
        }
    }

    fun closeFlashlight() {
        if (flashLightAvailable()) {
            mCameraConfigurationManager!!.closeFlashlight(mCamera!!)
        }
    }

    private fun flashLightAvailable(): Boolean {
        return isPreviewing && context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    fun onScanBoxRectChanged(scanRect: Rect?) {
        var scanRectTemp = scanRect
        if (mCamera == null || scanRectTemp == null || scanRectTemp.left <= 0 || scanRectTemp.top <= 0) {
            return
        }
        var centerX = scanRectTemp.centerX()
        var centerY = scanRectTemp.centerY()
        var rectHalfWidth = scanRectTemp.width() / 2
        var rectHalfHeight = scanRectTemp.height() / 2
        BGAQRCodeUtil.printRect("转换前", scanRectTemp)
        if (BGAQRCodeUtil.isPortrait(context)) {
            var temp = centerX
            centerX = centerY
            centerY = temp
            temp = rectHalfWidth
            rectHalfWidth = rectHalfHeight
            rectHalfHeight = temp
        }
        scanRectTemp = Rect(centerX - rectHalfWidth, centerY - rectHalfHeight, centerX + rectHalfWidth, centerY + rectHalfHeight)
        BGAQRCodeUtil. printRect("转换后", scanRectTemp)
        BGAQRCodeUtil.d("扫码框发生变化触发对焦测光")
        handleFocusMetering(scanRectTemp.centerX().toFloat(), scanRectTemp.centerY().toFloat(), scanRectTemp.width(), scanRectTemp.height())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isPreviewing) {
            return super.onTouchEvent(event)
        }
        if (event.pointerCount == 1 && event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
            if (mIsTouchFocusing) {
                return true
            }
            mIsTouchFocusing = true
            LogUtils.d("手指触摸触发对焦测光")
            var centerX = event.x
            var centerY = event.y
            if (BGAQRCodeUtil.isPortrait(context)) {
                val temp = centerX
                centerX = centerY
                centerY = temp
            }
            val focusSize = dp2px(120f)
            handleFocusMetering(centerX, centerY, focusSize, focusSize)
        }
        if (event.pointerCount == 2) {
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_POINTER_DOWN -> mOldDist = BGAQRCodeUtil.calculateFingerSpacing(event)
                MotionEvent.ACTION_MOVE -> {
                    val newDist = BGAQRCodeUtil.calculateFingerSpacing(event)
                    if (newDist > mOldDist) {
                        handleZoom(true, mCamera)
                    } else if (newDist < mOldDist) {
                        handleZoom(false, mCamera)
                    }
                }
            }
        }
        return true
    }

    private fun handleFocusMetering(originFocusCenterX: Float, originFocusCenterY: Float,
                                    originFocusWidth: Int, originFocusHeight: Int) {
        try {
            var isNeedUpdate = false
            val focusMeteringParameters = mCamera!!.parameters
            val size = focusMeteringParameters.previewSize
            if (focusMeteringParameters.maxNumFocusAreas > 0) {
                BGAQRCodeUtil.d("支持设置对焦区域")
                isNeedUpdate = true
                val focusRect = BGAQRCodeUtil.calculateFocusMeteringArea(1f,
                        originFocusCenterX, originFocusCenterY,
                        originFocusWidth, originFocusHeight,
                        size.width, size.height)
                BGAQRCodeUtil.printRect("对焦区域", focusRect)
                focusMeteringParameters.focusAreas = listOf(Camera.Area(focusRect, 1000))
                focusMeteringParameters.focusMode = Camera.Parameters.FOCUS_MODE_MACRO
            } else {
                BGAQRCodeUtil.d("不支持设置对焦区域")
            }
            if (focusMeteringParameters.maxNumMeteringAreas > 0) {
                BGAQRCodeUtil.d("支持设置测光区域")
                isNeedUpdate = true
                val meteringRect = BGAQRCodeUtil.calculateFocusMeteringArea(1.5f,
                        originFocusCenterX, originFocusCenterY,
                        originFocusWidth, originFocusHeight,
                        size.width, size.height)
                BGAQRCodeUtil.printRect("测光区域", meteringRect)
                focusMeteringParameters.meteringAreas = listOf(Camera.Area(meteringRect, 1000))
            } else {
                BGAQRCodeUtil.d("不支持设置测光区域")
            }
            if (isNeedUpdate) {
                mCamera!!.cancelAutoFocus()
                mCamera!!.parameters = focusMeteringParameters
                mCamera!!.autoFocus { success, camera ->
                    if (success) {
                        BGAQRCodeUtil.d("对焦测光成功")
                    } else {
                        BGAQRCodeUtil.e("对焦测光失败")
                    }
                    startContinuousAutoFocus()
                }
            } else {
                mIsTouchFocusing = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            BGAQRCodeUtil.e("对焦测光失败：" + e.message)
            startContinuousAutoFocus()
        }
    }

    /**
     * 连续对焦
     */
    private fun startContinuousAutoFocus() {
        mIsTouchFocusing = false
        if (mCamera == null) {
            return
        }
        try {
            val parameters = mCamera!!.parameters
            // 连续对焦
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            mCamera!!.parameters = parameters
            // 要实现连续的自动对焦，这一句必须加上
            mCamera!!.cancelAutoFocus()
        } catch (e: Exception) {
            BGAQRCodeUtil.e("连续对焦失败")
        }
    }

    val isPreviewing: Boolean
        get() = mCamera != null && mPreviewing && mSurfaceCreated

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        var height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        if (mCameraConfigurationManager != null && mCameraConfigurationManager!!.cameraResolution != null) {
            val cameraResolution: Point = mCameraConfigurationManager!!.cameraResolution!!
            // 取出来的cameraResolution高宽值与屏幕的高宽顺序是相反的
            val cameraPreviewWidth = cameraResolution.x
            val cameraPreviewHeight = cameraResolution.y
            if (width * 1f / height < cameraPreviewWidth * 1f / cameraPreviewHeight) {
                val ratio = cameraPreviewHeight * 1f / cameraPreviewWidth
                width = (height / ratio + 0.5f).toInt()
            } else {
                val ratio = cameraPreviewWidth * 1f / cameraPreviewHeight
                height = (width / ratio + 0.5f).toInt()
            }
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
    }

    interface Delegate {
        fun onStartPreview()
    }

    companion object {
        private fun handleZoom(isZoomIn: Boolean, camera: Camera?) {
            val params = camera!!.parameters
            if (params.isZoomSupported) {
                var zoom = params.zoom
                if (isZoomIn && zoom < params.maxZoom) {
                    BGAQRCodeUtil.d("放大")
                    zoom++
                } else if (!isZoomIn && zoom > 0) {
                    BGAQRCodeUtil.d("缩小")
                    zoom--
                } else {
                    BGAQRCodeUtil.d("既不放大也不缩小")
                }
                params.zoom = zoom
                camera.parameters = params
            } else {
                BGAQRCodeUtil.d("不支持缩放")
            }
        }
    }

    init {
        holder.addCallback(this)
    }
}
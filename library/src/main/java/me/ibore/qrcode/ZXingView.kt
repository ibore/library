package me.ibore.qrcode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import com.google.zxing.*
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer

class ZXingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : QRCodeView(context, attrs, defStyleAttr) {
    private var mMultiFormatReader: MultiFormatReader? = null
    private var mHintMap: Map<DecodeHintType, Any?>? = null

    override fun setupReader() {
        mMultiFormatReader = MultiFormatReader()
        when (mBarcodeType) {
            BarcodeType.ONE_DIMENSION -> {
                mMultiFormatReader!!.setHints(QRCodeDecoder.ONE_DIMENSION_HINT_MAP)
            }
            BarcodeType.TWO_DIMENSION -> {
                mMultiFormatReader!!.setHints(QRCodeDecoder.TWO_DIMENSION_HINT_MAP)
            }
            BarcodeType.ONLY_QR_CODE -> {
                mMultiFormatReader!!.setHints(QRCodeDecoder.QR_CODE_HINT_MAP)
            }
            BarcodeType.ONLY_CODE_128 -> {
                mMultiFormatReader!!.setHints(QRCodeDecoder.CODE_128_HINT_MAP)
            }
            BarcodeType.ONLY_EAN_13 -> {
                mMultiFormatReader!!.setHints(QRCodeDecoder.EAN_13_HINT_MAP)
            }
            BarcodeType.HIGH_FREQUENCY -> {
                mMultiFormatReader!!.setHints(QRCodeDecoder.HIGH_FREQUENCY_HINT_MAP)
            }
            BarcodeType.CUSTOM -> {
                mMultiFormatReader!!.setHints(mHintMap)
            }
            else -> {
                mMultiFormatReader!!.setHints(QRCodeDecoder.ALL_HINT_MAP)
            }
        }
    }

    /**
     * 设置识别的格式
     *
     * @param barcodeType 识别的格式
     * @param hintMap     barcodeType 为 BarcodeType.CUSTOM 时，必须指定该值
     */
    fun setType(barcodeType: BarcodeType, hintMap: Map<DecodeHintType, Any?>?) {
        mBarcodeType = barcodeType
        mHintMap = hintMap
        if (mBarcodeType == BarcodeType.CUSTOM && (mHintMap == null || mHintMap!!.isEmpty())) {
            throw RuntimeException("barcodeType 为 BarcodeType.CUSTOM 时 hintMap 不能为空")
        }
        setupReader()
    }

    override fun processBitmapData(bitmap: Bitmap?): ScanResult {
        return ScanResult(QRCodeDecoder.syncDecodeQRCode(bitmap!!))
    }

    override fun processData(data: ByteArray?, width: Int, height: Int, isRetry: Boolean): ScanResult? {
        var rawResult: Result? = null
        var scanBoxAreaRect: Rect? = null
        try {
            scanBoxAreaRect = scanBoxView.getScanBoxAreaRect(height)
            val source: PlanarYUVLuminanceSource = if (scanBoxAreaRect != null) {
                PlanarYUVLuminanceSource(data, width, height, scanBoxAreaRect.left, scanBoxAreaRect.top, scanBoxAreaRect.width(),
                        scanBoxAreaRect.height(), false)
            } else {
                PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false)
            }
            rawResult = mMultiFormatReader!!.decodeWithState(BinaryBitmap(GlobalHistogramBinarizer(source)))
            if (rawResult == null) {
                rawResult = mMultiFormatReader!!.decodeWithState(BinaryBitmap(HybridBinarizer(source)))
                if (rawResult != null) {
                    BGAQRCodeUtil.d("GlobalHistogramBinarizer 没识别到，HybridBinarizer 能识别到")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mMultiFormatReader!!.reset()
        }
        if (rawResult == null) {
            return null
        }
        val result = rawResult.text
        if (TextUtils.isEmpty(result)) {
            return null
        }
        val barcodeFormat = rawResult.barcodeFormat
        BGAQRCodeUtil.d("格式为：" + barcodeFormat.name)

        // 处理自动缩放和定位点
        val isNeedAutoZoom = isAutoZoom && barcodeFormat == BarcodeFormat.QR_CODE
        if (isShowLocationPoint || isNeedAutoZoom) {
            val resultPoints = rawResult.resultPoints
            val pointArr = arrayOf<PointF>()
            for ((pointIndex, resultPoint) in resultPoints.withIndex()) {
                pointArr[pointIndex] = PointF(resultPoint.x, resultPoint.y)
            }
            if (transformToViewCoordinates(pointArr, scanBoxAreaRect, isNeedAutoZoom, result)) {
                return null
            }
        }
        return ScanResult(result)
    }


}
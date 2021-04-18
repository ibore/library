package me.ibore.qrcode

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import me.ibore.R
import me.ibore.ktx.dp2px
import me.ibore.ktx.sp2px
import me.ibore.qrcode.BGAQRCodeUtil.adjustPhotoRotation
import me.ibore.qrcode.BGAQRCodeUtil.makeTintBitmap
import me.ibore.utils.BarUtils

class ScanBoxView(context: Context?) : View(context) {
    private var mMoveStepDistance: Int
    private var mAnimDelayTime = 0
    private var mFramingRect: Rect? = null
    private var mScanLineTop = 0f
    private var mScanLineLeft = 0f
    private val mPaint: Paint
    private val mTipPaint: TextPaint
    private var mMaskColor: Int
    private var mCornerColor: Int
    private var mCornerLength: Int
    private var mCornerSize: Int
    private var mRectWidth: Int
    private var mRectHeight = 0
    private var mBarcodeRectHeight: Int
    private var mTopOffset: Int
    private var mScanLineSize: Int
    private var mScanLineColor: Int
    private var mScanLineMargin: Int
    private var mIsShowDefaultScanLineDrawable: Boolean
    private var mCustomScanLineDrawable: Drawable?
    private var mScanLineBitmap: Bitmap?
    private var mBorderSize: Int
    private var mBorderColor: Int
    private var mAnimTime: Int
    private var mVerticalBias: Float
    private var mCornerDisplayType: Int
    private var mToolbarHeight: Int
    private var mIsBarcode: Boolean
    private var mQRCodeTipText: String? = null
    private var mBarCodeTipText: String? = null
    private var mTipText: String?
    private var mTipTextSize: Int
    private var mTipTextColor: Int
    private var mIsTipTextBelowRect: Boolean
    private var mTipTextMargin: Int
    private var mIsShowTipTextAsSingleLine: Boolean
    private var mTipBackgroundColor: Int
    private var mIsShowTipBackground: Boolean
    private var mIsScanLineReverse: Boolean
    private var mIsShowDefaultGridScanLineDrawable: Boolean
    private var mCustomGridScanLineDrawable: Drawable? = null
    private var mGridScanLineBitmap: Bitmap? = null
    private var mGridScanLineBottom = 0f
    private var mGridScanLineRight = 0f
    private var mOriginQRCodeScanLineBitmap: Bitmap? = null
    private var mOriginBarCodeScanLineBitmap: Bitmap? = null
    private var mOriginQRCodeGridScanLineBitmap: Bitmap? = null
    private var mOriginBarCodeGridScanLineBitmap: Bitmap? = null
    private var mHalfCornerSize = 0f
    private var mTipTextSl: StaticLayout? = null
    private var mTipBackgroundRadius: Int
    private var mIsOnlyDecodeScanBoxArea: Boolean
    var isShowLocationPoint: Boolean
    var isAutoZoom: Boolean
    private var mQRCodeView: QRCodeView? = null

    fun init(qrCodeView: QRCodeView?, attrs: AttributeSet?) {
        mQRCodeView = qrCodeView
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.QRCodeView)
        val count = typedArray.indexCount
        for (i in 0 until count) {
            initCustomAttr(typedArray.getIndex(i), typedArray)
        }
        typedArray.recycle()
        afterInitCustomAttrs()
    }

    private fun initCustomAttr(attr: Int, typedArray: TypedArray) {
        when (attr) {
            R.styleable.QRCodeView_qrcv_topOffset -> {
                mTopOffset = typedArray.getDimensionPixelSize(attr, mTopOffset)
            }
            R.styleable.QRCodeView_qrcv_cornerSize -> {
                mCornerSize = typedArray.getDimensionPixelSize(attr, mCornerSize)
            }
            R.styleable.QRCodeView_qrcv_cornerLength -> {
                mCornerLength = typedArray.getDimensionPixelSize(attr, mCornerLength)
            }
            R.styleable.QRCodeView_qrcv_scanLineSize -> {
                mScanLineSize = typedArray.getDimensionPixelSize(attr, mScanLineSize)
            }
            R.styleable.QRCodeView_qrcv_rectWidth -> {
                mRectWidth = typedArray.getDimensionPixelSize(attr, mRectWidth)
            }
            R.styleable.QRCodeView_qrcv_maskColor -> {
                mMaskColor = typedArray.getColor(attr, mMaskColor)
            }
            R.styleable.QRCodeView_qrcv_cornerColor -> {
                mCornerColor = typedArray.getColor(attr, mCornerColor)
            }
            R.styleable.QRCodeView_qrcv_scanLineColor -> {
                mScanLineColor = typedArray.getColor(attr, mScanLineColor)
            }
            R.styleable.QRCodeView_qrcv_scanLineMargin -> {
                mScanLineMargin = typedArray.getDimensionPixelSize(attr, mScanLineMargin)
            }
            R.styleable.QRCodeView_qrcv_isShowDefaultScanLineDrawable -> {
                mIsShowDefaultScanLineDrawable = typedArray.getBoolean(attr, mIsShowDefaultScanLineDrawable)
            }
            R.styleable.QRCodeView_qrcv_customScanLineDrawable -> {
                mCustomScanLineDrawable = typedArray.getDrawable(attr)
            }
            R.styleable.QRCodeView_qrcv_borderSize -> {
                mBorderSize = typedArray.getDimensionPixelSize(attr, mBorderSize)
            }
            R.styleable.QRCodeView_qrcv_borderColor -> {
                mBorderColor = typedArray.getColor(attr, mBorderColor)
            }
            R.styleable.QRCodeView_qrcv_animTime -> {
                mAnimTime = typedArray.getInteger(attr, mAnimTime)
            }
            R.styleable.QRCodeView_qrcv_verticalBias -> {
                mVerticalBias = typedArray.getFloat(attr, mVerticalBias)
            }
            R.styleable.QRCodeView_qrcv_cornerDisplayType -> {
                mCornerDisplayType = typedArray.getInteger(attr, mCornerDisplayType)
            }
            R.styleable.QRCodeView_qrcv_toolbarHeight -> {
                mToolbarHeight = typedArray.getDimensionPixelSize(attr, mToolbarHeight)
            }
            R.styleable.QRCodeView_qrcv_barcodeRectHeight -> {
                mBarcodeRectHeight = typedArray.getDimensionPixelSize(attr, mBarcodeRectHeight)
            }
            R.styleable.QRCodeView_qrcv_isBarcode -> {
                mIsBarcode = typedArray.getBoolean(attr, mIsBarcode)
            }
            R.styleable.QRCodeView_qrcv_barCodeTipText -> {
                mBarCodeTipText = typedArray.getString(attr)
            }
            R.styleable.QRCodeView_qrcv_qrCodeTipText -> {
                mQRCodeTipText = typedArray.getString(attr)
            }
            R.styleable.QRCodeView_qrcv_tipTextSize -> {
                mTipTextSize = typedArray.getDimensionPixelSize(attr, mTipTextSize)
            }
            R.styleable.QRCodeView_qrcv_tipTextColor -> {
                mTipTextColor = typedArray.getColor(attr, mTipTextColor)
            }
            R.styleable.QRCodeView_qrcv_isTipTextBelowRect -> {
                mIsTipTextBelowRect = typedArray.getBoolean(attr, mIsTipTextBelowRect)
            }
            R.styleable.QRCodeView_qrcv_tipTextMargin -> {
                mTipTextMargin = typedArray.getDimensionPixelSize(attr, mTipTextMargin)
            }
            R.styleable.QRCodeView_qrcv_isShowTipTextAsSingleLine -> {
                mIsShowTipTextAsSingleLine = typedArray.getBoolean(attr, mIsShowTipTextAsSingleLine)
            }
            R.styleable.QRCodeView_qrcv_isShowTipBackground -> {
                mIsShowTipBackground = typedArray.getBoolean(attr, mIsShowTipBackground)
            }
            R.styleable.QRCodeView_qrcv_tipBackgroundColor -> {
                mTipBackgroundColor = typedArray.getColor(attr, mTipBackgroundColor)
            }
            R.styleable.QRCodeView_qrcv_isScanLineReverse -> {
                mIsScanLineReverse = typedArray.getBoolean(attr, mIsScanLineReverse)
            }
            R.styleable.QRCodeView_qrcv_isShowDefaultGridScanLineDrawable -> {
                mIsShowDefaultGridScanLineDrawable = typedArray.getBoolean(attr, mIsShowDefaultGridScanLineDrawable)
            }
            R.styleable.QRCodeView_qrcv_customGridScanLineDrawable -> {
                mCustomGridScanLineDrawable = typedArray.getDrawable(attr)
            }
            R.styleable.QRCodeView_qrcv_isOnlyDecodeScanBoxArea -> {
                mIsOnlyDecodeScanBoxArea = typedArray.getBoolean(attr, mIsOnlyDecodeScanBoxArea)
            }
            R.styleable.QRCodeView_qrcv_isShowLocationPoint -> {
                isShowLocationPoint = typedArray.getBoolean(attr, isShowLocationPoint)
            }
            R.styleable.QRCodeView_qrcv_isAutoZoom -> {
                isAutoZoom = typedArray.getBoolean(attr, isAutoZoom)
            }
        }
    }

    private fun afterInitCustomAttrs() {
        if (mCustomGridScanLineDrawable != null) {
            mOriginQRCodeGridScanLineBitmap = (mCustomGridScanLineDrawable as BitmapDrawable).bitmap
        }
        if (mOriginQRCodeGridScanLineBitmap == null) {
            mOriginQRCodeGridScanLineBitmap = BitmapFactory.decodeResource(resources, R.drawable.qrcode_default_grid_scan_line)
            mOriginQRCodeGridScanLineBitmap = makeTintBitmap(mOriginQRCodeGridScanLineBitmap, mScanLineColor)
        }
        mOriginBarCodeGridScanLineBitmap = adjustPhotoRotation(mOriginQRCodeGridScanLineBitmap, 90)
        mOriginBarCodeGridScanLineBitmap = adjustPhotoRotation(mOriginBarCodeGridScanLineBitmap, 90)
        mOriginBarCodeGridScanLineBitmap = adjustPhotoRotation(mOriginBarCodeGridScanLineBitmap, 90)
        if (mCustomScanLineDrawable != null) {
            mOriginQRCodeScanLineBitmap = (mCustomScanLineDrawable as BitmapDrawable).bitmap
        }
        if (mOriginQRCodeScanLineBitmap == null) {
            mOriginQRCodeScanLineBitmap = BitmapFactory.decodeResource(resources, R.drawable.qrcode_default_scan_line)
            mOriginQRCodeScanLineBitmap = makeTintBitmap(mOriginQRCodeScanLineBitmap, mScanLineColor)
        }
        mOriginBarCodeScanLineBitmap = adjustPhotoRotation(mOriginQRCodeScanLineBitmap, 90)
        mTopOffset += mToolbarHeight
        mHalfCornerSize = 1.0f * mCornerSize / 2
        mTipPaint.textSize = mTipTextSize.toFloat()
        mTipPaint.color = mTipTextColor
        isBarcode = mIsBarcode
    }

    public override fun onDraw(canvas: Canvas) {
        if (mFramingRect == null) {
            return
        }

        // 画遮罩层
        drawMask(canvas)

        // 画边框线
        drawBorderLine(canvas)

        // 画四个直角的线
        drawCornerLine(canvas)

        // 画扫描线
        drawScanLine(canvas)

        // 画提示文本
        drawTipText(canvas)

        // 移动扫描线的位置
        moveScanLine()
    }

    /**
     * 画遮罩层
     */
    private fun drawMask(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        if (mMaskColor != Color.TRANSPARENT) {
            mPaint.style = Paint.Style.FILL
            mPaint.color = mMaskColor
            canvas.drawRect(0f, 0f, width.toFloat(), mFramingRect!!.top.toFloat(), mPaint)
            canvas.drawRect(0f, mFramingRect!!.top.toFloat(), mFramingRect!!.left.toFloat(), mFramingRect!!.bottom + 1.toFloat(), mPaint)
            canvas.drawRect(mFramingRect!!.right + 1.toFloat(), mFramingRect!!.top.toFloat(), width.toFloat(), mFramingRect!!.bottom + 1.toFloat(), mPaint)
            canvas.drawRect(0f, mFramingRect!!.bottom + 1.toFloat(), width.toFloat(), height.toFloat(), mPaint)
        }
    }

    /**
     * 画边框线
     */
    private fun drawBorderLine(canvas: Canvas) {
        if (mBorderSize > 0) {
            mPaint.style = Paint.Style.STROKE
            mPaint.color = mBorderColor
            mPaint.strokeWidth = mBorderSize.toFloat()
            canvas.drawRect(mFramingRect!!, mPaint)
        }
    }

    /**
     * 画四个直角的线
     */
    private fun drawCornerLine(canvas: Canvas) {
        if (mHalfCornerSize > 0) {
            mPaint.style = Paint.Style.STROKE
            mPaint.color = mCornerColor
            mPaint.strokeWidth = mCornerSize.toFloat()
            if (mCornerDisplayType == 1) {
                canvas.drawLine(mFramingRect!!.left - mHalfCornerSize, mFramingRect!!.top.toFloat(), mFramingRect!!.left - mHalfCornerSize + mCornerLength, mFramingRect!!.top.toFloat(),
                        mPaint)
                canvas.drawLine(mFramingRect!!.left.toFloat(), mFramingRect!!.top - mHalfCornerSize, mFramingRect!!.left.toFloat(), mFramingRect!!.top - mHalfCornerSize + mCornerLength,
                        mPaint)
                canvas.drawLine(mFramingRect!!.right + mHalfCornerSize, mFramingRect!!.top.toFloat(), mFramingRect!!.right + mHalfCornerSize - mCornerLength, mFramingRect!!.top.toFloat(),
                        mPaint)
                canvas.drawLine(mFramingRect!!.right.toFloat(), mFramingRect!!.top - mHalfCornerSize, mFramingRect!!.right.toFloat(), mFramingRect!!.top - mHalfCornerSize + mCornerLength,
                        mPaint)
                canvas.drawLine(mFramingRect!!.left - mHalfCornerSize, mFramingRect!!.bottom.toFloat(), mFramingRect!!.left - mHalfCornerSize + mCornerLength,
                        mFramingRect!!.bottom.toFloat(), mPaint)
                canvas.drawLine(mFramingRect!!.left.toFloat(), mFramingRect!!.bottom + mHalfCornerSize, mFramingRect!!.left.toFloat(),
                        mFramingRect!!.bottom + mHalfCornerSize - mCornerLength, mPaint)
                canvas.drawLine(mFramingRect!!.right + mHalfCornerSize, mFramingRect!!.bottom.toFloat(), mFramingRect!!.right + mHalfCornerSize - mCornerLength,
                        mFramingRect!!.bottom.toFloat(), mPaint)
                canvas.drawLine(mFramingRect!!.right.toFloat(), mFramingRect!!.bottom + mHalfCornerSize, mFramingRect!!.right.toFloat(),
                        mFramingRect!!.bottom + mHalfCornerSize - mCornerLength, mPaint)
            } else if (mCornerDisplayType == 2) {
                canvas.drawLine(mFramingRect!!.left.toFloat(), mFramingRect!!.top + mHalfCornerSize, mFramingRect!!.left + mCornerLength.toFloat(), mFramingRect!!.top + mHalfCornerSize,
                        mPaint)
                canvas.drawLine(mFramingRect!!.left + mHalfCornerSize, mFramingRect!!.top.toFloat(), mFramingRect!!.left + mHalfCornerSize, mFramingRect!!.top + mCornerLength.toFloat(),
                        mPaint)
                canvas.drawLine(mFramingRect!!.right.toFloat(), mFramingRect!!.top + mHalfCornerSize, mFramingRect!!.right - mCornerLength.toFloat(), mFramingRect!!.top + mHalfCornerSize,
                        mPaint)
                canvas.drawLine(mFramingRect!!.right - mHalfCornerSize, mFramingRect!!.top.toFloat(), mFramingRect!!.right - mHalfCornerSize, mFramingRect!!.top + mCornerLength.toFloat(),
                        mPaint)
                canvas.drawLine(mFramingRect!!.left.toFloat(), mFramingRect!!.bottom - mHalfCornerSize, mFramingRect!!.left + mCornerLength.toFloat(),
                        mFramingRect!!.bottom - mHalfCornerSize, mPaint)
                canvas.drawLine(mFramingRect!!.left + mHalfCornerSize, mFramingRect!!.bottom.toFloat(), mFramingRect!!.left + mHalfCornerSize,
                        mFramingRect!!.bottom - mCornerLength.toFloat(), mPaint)
                canvas.drawLine(mFramingRect!!.right.toFloat(), mFramingRect!!.bottom - mHalfCornerSize, mFramingRect!!.right - mCornerLength.toFloat(),
                        mFramingRect!!.bottom - mHalfCornerSize, mPaint)
                canvas.drawLine(mFramingRect!!.right - mHalfCornerSize, mFramingRect!!.bottom.toFloat(), mFramingRect!!.right - mHalfCornerSize,
                        mFramingRect!!.bottom - mCornerLength.toFloat(), mPaint)
            }
        }
    }

    /**
     * 画扫描线
     */
    private fun drawScanLine(canvas: Canvas) {
        if (mIsBarcode) {
            if (mGridScanLineBitmap != null) {
                val dstGridRectF = RectF(mFramingRect!!.left + mHalfCornerSize + 0.5f, mFramingRect!!.top + mHalfCornerSize + mScanLineMargin,
                        mGridScanLineRight, mFramingRect!!.bottom - mHalfCornerSize - mScanLineMargin)
                val srcGridRect = Rect((mGridScanLineBitmap!!.width - dstGridRectF.width()).toInt(), 0, mGridScanLineBitmap!!.width,
                        mGridScanLineBitmap!!.height)
                if (srcGridRect.left < 0) {
                    srcGridRect.left = 0
                    dstGridRectF.left = dstGridRectF.right - srcGridRect.width()
                }
                canvas.drawBitmap(mGridScanLineBitmap!!, srcGridRect, dstGridRectF, mPaint)
            } else if (mScanLineBitmap != null) {
                val lineRect = RectF(mScanLineLeft, mFramingRect!!.top + mHalfCornerSize + mScanLineMargin, mScanLineLeft + mScanLineBitmap!!.width,
                        mFramingRect!!.bottom - mHalfCornerSize - mScanLineMargin)
                canvas.drawBitmap(mScanLineBitmap!!, null, lineRect, mPaint)
            } else {
                mPaint.style = Paint.Style.FILL
                mPaint.color = mScanLineColor
                canvas.drawRect(mScanLineLeft, mFramingRect!!.top + mHalfCornerSize + mScanLineMargin, mScanLineLeft + mScanLineSize,
                        mFramingRect!!.bottom - mHalfCornerSize - mScanLineMargin, mPaint)
            }
        } else {
            if (mGridScanLineBitmap != null) {
                val dstGridRectF = RectF(mFramingRect!!.left + mHalfCornerSize + mScanLineMargin, mFramingRect!!.top + mHalfCornerSize + 0.5f,
                        mFramingRect!!.right - mHalfCornerSize - mScanLineMargin, mGridScanLineBottom)
                val srcRect = Rect(0, (mGridScanLineBitmap!!.height - dstGridRectF.height()).toInt(), mGridScanLineBitmap!!.width,
                        mGridScanLineBitmap!!.height)
                if (srcRect.top < 0) {
                    srcRect.top = 0
                    dstGridRectF.top = dstGridRectF.bottom - srcRect.height()
                }
                canvas.drawBitmap(mGridScanLineBitmap!!, srcRect, dstGridRectF, mPaint)
            } else if (mScanLineBitmap != null) {
                val lineRect = RectF(mFramingRect!!.left + mHalfCornerSize + mScanLineMargin, mScanLineTop,
                        mFramingRect!!.right - mHalfCornerSize - mScanLineMargin, mScanLineTop + mScanLineBitmap!!.height)
                canvas.drawBitmap(mScanLineBitmap!!, null, lineRect, mPaint)
            } else {
                mPaint.style = Paint.Style.FILL
                mPaint.color = mScanLineColor
                canvas.drawRect(mFramingRect!!.left + mHalfCornerSize + mScanLineMargin, mScanLineTop, mFramingRect!!.right - mHalfCornerSize - mScanLineMargin,
                        mScanLineTop + mScanLineSize, mPaint)
            }
        }
    }

    /**
     * 画提示文本
     */
    private fun drawTipText(canvas: Canvas) {
        if (TextUtils.isEmpty(mTipText) || mTipTextSl == null) {
            return
        }
        if (mIsTipTextBelowRect) {
            if (mIsShowTipBackground) {
                mPaint.color = mTipBackgroundColor
                mPaint.style = Paint.Style.FILL
                if (mIsShowTipTextAsSingleLine) {
                    val tipRect = Rect()
                    mTipPaint.getTextBounds(mTipText, 0, mTipText!!.length, tipRect)
                    val left = (canvas.width - tipRect.width()) / 2 - mTipBackgroundRadius.toFloat()
                    canvas.drawRoundRect(
                            RectF(left, (mFramingRect!!.bottom + mTipTextMargin - mTipBackgroundRadius).toFloat(), left + tipRect.width() + 2 * mTipBackgroundRadius,
                                    (mFramingRect!!.bottom + mTipTextMargin + mTipTextSl!!.height + mTipBackgroundRadius).toFloat()), mTipBackgroundRadius.toFloat(),
                            mTipBackgroundRadius.toFloat(), mPaint)
                } else {
                    canvas.drawRoundRect(RectF(mFramingRect!!.left.toFloat(), (mFramingRect!!.bottom + mTipTextMargin - mTipBackgroundRadius).toFloat(), mFramingRect!!.right.toFloat(),
                            (mFramingRect!!.bottom + mTipTextMargin + mTipTextSl!!.height + mTipBackgroundRadius).toFloat()), mTipBackgroundRadius.toFloat(),
                            mTipBackgroundRadius.toFloat(),
                            mPaint)
                }
            }
            canvas.save()
            if (mIsShowTipTextAsSingleLine) {
                canvas.translate(0f, mFramingRect!!.bottom + mTipTextMargin.toFloat())
            } else {
                canvas.translate(mFramingRect!!.left + mTipBackgroundRadius.toFloat(), mFramingRect!!.bottom + mTipTextMargin.toFloat())
            }
            mTipTextSl!!.draw(canvas)
            canvas.restore()
        } else {
            if (mIsShowTipBackground) {
                mPaint.color = mTipBackgroundColor
                mPaint.style = Paint.Style.FILL
                if (mIsShowTipTextAsSingleLine) {
                    val tipRect = Rect()
                    mTipPaint.getTextBounds(mTipText, 0, mTipText!!.length, tipRect)
                    val left = (canvas.width - tipRect.width()) / 2 - mTipBackgroundRadius.toFloat()
                    canvas.drawRoundRect(RectF(left, (mFramingRect!!.top - mTipTextMargin - mTipTextSl!!.height - mTipBackgroundRadius).toFloat(),
                            left + tipRect.width() + 2 * mTipBackgroundRadius, (mFramingRect!!.top - mTipTextMargin + mTipBackgroundRadius).toFloat()),
                            mTipBackgroundRadius.toFloat(),
                            mTipBackgroundRadius.toFloat(), mPaint)
                } else {
                    canvas.drawRoundRect(
                            RectF(mFramingRect!!.left.toFloat(), (mFramingRect!!.top - mTipTextMargin - mTipTextSl!!.height - mTipBackgroundRadius).toFloat(), mFramingRect!!.right.toFloat(),
                                    (mFramingRect!!.top - mTipTextMargin + mTipBackgroundRadius).toFloat()), mTipBackgroundRadius.toFloat(), mTipBackgroundRadius.toFloat(), mPaint)
                }
            }
            canvas.save()
            if (mIsShowTipTextAsSingleLine) {
                canvas.translate(0f, mFramingRect!!.top - mTipTextMargin - mTipTextSl!!.height.toFloat())
            } else {
                canvas.translate(mFramingRect!!.left + mTipBackgroundRadius.toFloat(), mFramingRect!!.top - mTipTextMargin - mTipTextSl!!.height.toFloat())
            }
            mTipTextSl!!.draw(canvas)
            canvas.restore()
        }
    }

    /**
     * 移动扫描线的位置
     */
    private fun moveScanLine() {
        if (mIsBarcode) {
            if (mGridScanLineBitmap == null) {
                // 处理非网格扫描图片的情况
                mScanLineLeft += mMoveStepDistance.toFloat()
                var scanLineSize = mScanLineSize
                if (mScanLineBitmap != null) {
                    scanLineSize = mScanLineBitmap!!.width
                }
                if (mIsScanLineReverse) {
                    if (mScanLineLeft + scanLineSize > mFramingRect!!.right - mHalfCornerSize || mScanLineLeft < mFramingRect!!.left + mHalfCornerSize) {
                        mMoveStepDistance = -mMoveStepDistance
                    }
                } else {
                    if (mScanLineLeft + scanLineSize > mFramingRect!!.right - mHalfCornerSize) {
                        mScanLineLeft = mFramingRect!!.left + mHalfCornerSize + 0.5f
                    }
                }
            } else {
                // 处理网格扫描图片的情况
                mGridScanLineRight += mMoveStepDistance.toFloat()
                if (mGridScanLineRight > mFramingRect!!.right - mHalfCornerSize) {
                    mGridScanLineRight = mFramingRect!!.left + mHalfCornerSize + 0.5f
                }
            }
        } else {
            if (mGridScanLineBitmap == null) {
                // 处理非网格扫描图片的情况
                mScanLineTop += mMoveStepDistance.toFloat()
                var scanLineSize = mScanLineSize
                if (mScanLineBitmap != null) {
                    scanLineSize = mScanLineBitmap!!.height
                }
                if (mIsScanLineReverse) {
                    if (mScanLineTop + scanLineSize > mFramingRect!!.bottom - mHalfCornerSize || mScanLineTop < mFramingRect!!.top + mHalfCornerSize) {
                        mMoveStepDistance = -mMoveStepDistance
                    }
                } else {
                    if (mScanLineTop + scanLineSize > mFramingRect!!.bottom - mHalfCornerSize) {
                        mScanLineTop = mFramingRect!!.top + mHalfCornerSize + 0.5f
                    }
                }
            } else {
                // 处理网格扫描图片的情况
                mGridScanLineBottom += mMoveStepDistance.toFloat()
                if (mGridScanLineBottom > mFramingRect!!.bottom - mHalfCornerSize) {
                    mGridScanLineBottom = mFramingRect!!.top + mHalfCornerSize + 0.5f
                }
            }
        }
        postInvalidateDelayed(mAnimDelayTime.toLong(), mFramingRect!!.left, mFramingRect!!.top, mFramingRect!!.right, mFramingRect!!.bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calFramingRect()
    }

    private fun calFramingRect() {
        val leftOffset = (width - mRectWidth) / 2
        mFramingRect = Rect(leftOffset, mTopOffset, leftOffset + mRectWidth, mTopOffset + mRectHeight)
        if (mIsBarcode) {
            mScanLineLeft = mFramingRect!!.left + mHalfCornerSize + 0.5f
            mGridScanLineRight = mScanLineLeft
        } else {
            mScanLineTop = mFramingRect!!.top + mHalfCornerSize + 0.5f
            mGridScanLineBottom = mScanLineTop
        }
        if (mQRCodeView != null && isOnlyDecodeScanBoxArea) {
            mQRCodeView!!.onScanBoxRectChanged(Rect(mFramingRect))
        }
    }

    fun getScanBoxAreaRect(previewHeight: Int): Rect? {
        return if (mIsOnlyDecodeScanBoxArea && visibility == VISIBLE) {
            val rect = Rect(mFramingRect)
            val ratio = 1.0f * previewHeight / measuredHeight
            val centerX = rect.exactCenterX() * ratio
            val centerY = rect.exactCenterY() * ratio
            val halfWidth = rect.width() / 2f
            val halfHeight = rect.height() / 2f
            val newHalfWidth = halfWidth * ratio
            val newHalfHeight = halfHeight * ratio
            rect.left = (centerX - newHalfWidth).toInt()
            rect.right = (centerX + newHalfWidth).toInt()
            rect.top = (centerY - newHalfHeight).toInt()
            rect.bottom = (centerY + newHalfHeight).toInt()
            rect
        } else {
            null
        }
    }

    private fun refreshScanBox() {
        if (mCustomGridScanLineDrawable != null || mIsShowDefaultGridScanLineDrawable) {
            mGridScanLineBitmap = if (mIsBarcode) {
                mOriginBarCodeGridScanLineBitmap
            } else {
                mOriginQRCodeGridScanLineBitmap
            }
        } else if (mCustomScanLineDrawable != null || mIsShowDefaultScanLineDrawable) {
            mScanLineBitmap = if (mIsBarcode) {
                mOriginBarCodeScanLineBitmap
            } else {
                mOriginQRCodeScanLineBitmap
            }
        }
        if (mIsBarcode) {
            mTipText = mBarCodeTipText
            mRectHeight = mBarcodeRectHeight
            mAnimDelayTime = (1.0f * mAnimTime * mMoveStepDistance / mRectWidth).toInt()
        } else {
            mTipText = mQRCodeTipText
            mRectHeight = mRectWidth
            mAnimDelayTime = (1.0f * mAnimTime * mMoveStepDistance / mRectHeight).toInt()
        }
        if (!TextUtils.isEmpty(mTipText)) {
            mTipTextSl = if (mIsShowTipTextAsSingleLine) {
                StaticLayout(mTipText, mTipPaint, BGAQRCodeUtil.getScreenResolution(context).x, Layout.Alignment.ALIGN_CENTER, 1.0f, 0F,
                        true)
            } else {
                StaticLayout(mTipText, mTipPaint, mRectWidth - 2 * mTipBackgroundRadius, Layout.Alignment.ALIGN_CENTER, 1.0f, 0F, true)
            }
        }
        if (mVerticalBias != -1f) {
            val screenHeight = BGAQRCodeUtil.getScreenResolution(context).y - BarUtils.getStatusBarHeight(context)
            mTopOffset = if (mToolbarHeight == 0) {
                (screenHeight * mVerticalBias - mRectHeight / 2).toInt()
            } else {
                mToolbarHeight + ((screenHeight - mToolbarHeight) * mVerticalBias - mRectHeight / 2).toInt()
            }
        }
        calFramingRect()
        postInvalidate()
    }

    var isBarcode: Boolean
        get() = mIsBarcode
        set(isBarcode) {
            mIsBarcode = isBarcode
            refreshScanBox()
        }
    var maskColor: Int
        get() = mMaskColor
        set(maskColor) {
            mMaskColor = maskColor
            refreshScanBox()
        }
    var cornerColor: Int
        get() = mCornerColor
        set(cornerColor) {
            mCornerColor = cornerColor
            refreshScanBox()
        }
    var cornerLength: Int
        get() = mCornerLength
        set(cornerLength) {
            mCornerLength = cornerLength
            refreshScanBox()
        }
    var cornerSize: Int
        get() = mCornerSize
        set(cornerSize) {
            mCornerSize = cornerSize
            refreshScanBox()
        }
    var rectWidth: Int
        get() = mRectWidth
        set(rectWidth) {
            mRectWidth = rectWidth
            refreshScanBox()
        }
    var rectHeight: Int
        get() = mRectHeight
        set(rectHeight) {
            mRectHeight = rectHeight
            refreshScanBox()
        }
    var barcodeRectHeight: Int
        get() = mBarcodeRectHeight
        set(barcodeRectHeight) {
            mBarcodeRectHeight = barcodeRectHeight
            refreshScanBox()
        }
    var topOffset: Int
        get() = mTopOffset
        set(topOffset) {
            mTopOffset = topOffset
            refreshScanBox()
        }
    var scanLineSize: Int
        get() = mScanLineSize
        set(scanLineSize) {
            mScanLineSize = scanLineSize
            refreshScanBox()
        }
    var scanLineColor: Int
        get() = mScanLineColor
        set(scanLineColor) {
            mScanLineColor = scanLineColor
            refreshScanBox()
        }
    var scanLineMargin: Int
        get() = mScanLineMargin
        set(scanLineMargin) {
            mScanLineMargin = scanLineMargin
            refreshScanBox()
        }
    var isShowDefaultScanLineDrawable: Boolean
        get() = mIsShowDefaultScanLineDrawable
        set(showDefaultScanLineDrawable) {
            mIsShowDefaultScanLineDrawable = showDefaultScanLineDrawable
            refreshScanBox()
        }
    var customScanLineDrawable: Drawable?
        get() = mCustomScanLineDrawable
        set(customScanLineDrawable) {
            mCustomScanLineDrawable = customScanLineDrawable
            refreshScanBox()
        }
    var scanLineBitmap: Bitmap?
        get() = mScanLineBitmap
        set(scanLineBitmap) {
            mScanLineBitmap = scanLineBitmap
            refreshScanBox()
        }
    var borderSize: Int
        get() = mBorderSize
        set(borderSize) {
            mBorderSize = borderSize
            refreshScanBox()
        }
    var borderColor: Int
        get() = mBorderColor
        set(borderColor) {
            mBorderColor = borderColor
            refreshScanBox()
        }
    var animTime: Int
        get() = mAnimTime
        set(animTime) {
            mAnimTime = animTime
            refreshScanBox()
        }
    var verticalBias: Float
        get() = mVerticalBias
        set(verticalBias) {
            mVerticalBias = verticalBias
            refreshScanBox()
        }
    var toolbarHeight: Int
        get() = mToolbarHeight
        set(toolbarHeight) {
            mToolbarHeight = toolbarHeight
            refreshScanBox()
        }
    var qRCodeTipText: String?
        get() = mQRCodeTipText
        set(qrCodeTipText) {
            mQRCodeTipText = qrCodeTipText
            refreshScanBox()
        }
    var barCodeTipText: String?
        get() = mBarCodeTipText
        set(barCodeTipText) {
            mBarCodeTipText = barCodeTipText
            refreshScanBox()
        }
    var tipText: String?
        get() = mTipText
        set(tipText) {
            if (mIsBarcode) {
                mBarCodeTipText = tipText
            } else {
                mQRCodeTipText = tipText
            }
            refreshScanBox()
        }
    var tipTextColor: Int
        get() = mTipTextColor
        set(tipTextColor) {
            mTipTextColor = tipTextColor
            mTipPaint.color = mTipTextColor
            refreshScanBox()
        }
    var tipTextSize: Int
        get() = mTipTextSize
        set(tipTextSize) {
            mTipTextSize = tipTextSize
            mTipPaint.textSize = mTipTextSize.toFloat()
            refreshScanBox()
        }
    var isTipTextBelowRect: Boolean
        get() = mIsTipTextBelowRect
        set(tipTextBelowRect) {
            mIsTipTextBelowRect = tipTextBelowRect
            refreshScanBox()
        }
    var tipTextMargin: Int
        get() = mTipTextMargin
        set(tipTextMargin) {
            mTipTextMargin = tipTextMargin
            refreshScanBox()
        }
    var isShowTipTextAsSingleLine: Boolean
        get() = mIsShowTipTextAsSingleLine
        set(showTipTextAsSingleLine) {
            mIsShowTipTextAsSingleLine = showTipTextAsSingleLine
            refreshScanBox()
        }
    var isShowTipBackground: Boolean
        get() = mIsShowTipBackground
        set(showTipBackground) {
            mIsShowTipBackground = showTipBackground
            refreshScanBox()
        }
    var tipBackgroundColor: Int
        get() = mTipBackgroundColor
        set(tipBackgroundColor) {
            mTipBackgroundColor = tipBackgroundColor
            refreshScanBox()
        }
    var isScanLineReverse: Boolean
        get() = mIsScanLineReverse
        set(scanLineReverse) {
            mIsScanLineReverse = scanLineReverse
            refreshScanBox()
        }
    var isShowDefaultGridScanLineDrawable: Boolean
        get() = mIsShowDefaultGridScanLineDrawable
        set(showDefaultGridScanLineDrawable) {
            mIsShowDefaultGridScanLineDrawable = showDefaultGridScanLineDrawable
            refreshScanBox()
        }
    var halfCornerSize: Float
        get() = mHalfCornerSize
        set(halfCornerSize) {
            mHalfCornerSize = halfCornerSize
            refreshScanBox()
        }
    var tipTextSl: StaticLayout?
        get() = mTipTextSl
        set(tipTextSl) {
            mTipTextSl = tipTextSl
            refreshScanBox()
        }
    var tipBackgroundRadius: Int
        get() = mTipBackgroundRadius
        set(tipBackgroundRadius) {
            mTipBackgroundRadius = tipBackgroundRadius
            refreshScanBox()
        }
    var isOnlyDecodeScanBoxArea: Boolean
        get() = mIsOnlyDecodeScanBoxArea
        set(onlyDecodeScanBoxArea) {
            mIsOnlyDecodeScanBoxArea = onlyDecodeScanBoxArea
            calFramingRect()
        }

    init {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mMaskColor = Color.parseColor("#33FFFFFF")
        mCornerColor = Color.WHITE
        mCornerLength = dp2px( 20f)
        mCornerSize = dp2px( 3f)
        mScanLineSize = dp2px( 1f)
        mScanLineColor = Color.WHITE
        mTopOffset = dp2px( 90f)
        mRectWidth = dp2px( 200f)
        mBarcodeRectHeight = dp2px( 140f)
        mScanLineMargin = 0
        mIsShowDefaultScanLineDrawable = false
        mCustomScanLineDrawable = null
        mScanLineBitmap = null
        mBorderSize = dp2px( 1f)
        mBorderColor = Color.WHITE
        mAnimTime = 1000
        mVerticalBias = -1f
        mCornerDisplayType = 1
        mToolbarHeight = 0
        mIsBarcode = false
        mMoveStepDistance = dp2px( 2f)
        mTipText = null
        mTipTextSize = sp2px( 14f)
        mTipTextColor = Color.WHITE
        mIsTipTextBelowRect = false
        mTipTextMargin = dp2px( 20f)
        mIsShowTipTextAsSingleLine = false
        mTipBackgroundColor = Color.parseColor("#22000000")
        mIsShowTipBackground = false
        mIsScanLineReverse = false
        mIsShowDefaultGridScanLineDrawable = false
        mTipPaint = TextPaint()
        mTipPaint.isAntiAlias = true
        mTipBackgroundRadius = dp2px( 4f)
        mIsOnlyDecodeScanBoxArea = false
        isShowLocationPoint = false
        isAutoZoom = false
    }
}
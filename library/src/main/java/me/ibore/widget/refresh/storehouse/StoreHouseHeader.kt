package me.ibore.widget.refresh.storehouse

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.Transformation
import me.ibore.ktx.dp2px
import me.ibore.utils.UIUtils
import me.ibore.utils.UIUtils.getScreenWidth
import me.ibore.widget.refresh.RefreshHeader
import me.ibore.widget.refresh.RefreshLayout
import java.util.*
import kotlin.math.ceil

class StoreHouseHeader : View, RefreshHeader {
    var mItemList: ArrayList<StoreHouseBarItem> = ArrayList<StoreHouseBarItem>()
    private var mLineWidth = -1
    var scale = 1f
    private var mDropHeight = -1
    private val mInternalAnimationFactor = 0.7f
    private var mHorizontalRandomness = -1
    private var mProgress = 0f
    private var mDrawZoneWidth = 0
    private var mDrawZoneHeight = 0
    private var mOffsetX = 0
    private var mOffsetY = 0
    private val mBarDarkAlpha = 0.4f
    private val mFromAlpha = 1.0f
    private val mToAlpha = 0.4f
    private var mLoadingAniDuration = 1000
    private var mLoadingAniSegDuration = 1000
    private val mLoadingAniItemDuration = 400L
    private val mTransformation = Transformation()
    private var mIsInLoading = false
    private val mAniController: AniController = AniController()
    private var mTextColor = Color.WHITE

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        mLineWidth = dp2px(1F)
        mDropHeight = dp2px(40F)
        mHorizontalRandomness = getScreenWidth(context) / 2
        initWithString("REFRESHLAYOUT", 25)
        setPadding(0, 20, 0, 20)
    }

    private fun setProgress(progress: Float) {
        mProgress = progress
    }

    var loadingAniDuration: Int
        get() = mLoadingAniDuration
        set(duration) {
            mLoadingAniDuration = duration
            mLoadingAniSegDuration = duration
        }

    fun setLineWidth(width: Int): StoreHouseHeader {
        mLineWidth = width
        for (i in mItemList.indices) {
            mItemList[i].setLineWidth(width)
        }
        return this
    }

    fun setTextColor(color: Int): StoreHouseHeader {
        mTextColor = color
        for (i in mItemList.indices) {
            mItemList[i].setColor(color)
        }
        return this
    }

    fun setDropHeight(height: Int): StoreHouseHeader {
        mDropHeight = height
        return this
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val tempHeightMeasureSpec: Int
        val height = topOffset + mDrawZoneHeight + bottomOffset
        tempHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, tempHeightMeasureSpec)
        mOffsetX = (measuredWidth - mDrawZoneWidth) / 2
        mOffsetY = topOffset
        mDropHeight = topOffset
    }

    private val topOffset: Int
        get() = paddingTop + dp2px(10F)
    private val bottomOffset: Int
        get() = paddingBottom + dp2px(10F)

    @JvmOverloads
    fun initWithString(str: String?, fontSize: Int = 25) {
        val pointList = StoreHousePath.getPath(str!!, fontSize * 0.01f, 14)
        initWithPointList(pointList)
    }

    fun initWithStringArray(id: Int) {
        val points = resources.getStringArray(id)
        val pointList = ArrayList<FloatArray>()
        for (i in points.indices) {
            val x = points[i].split(",".toRegex()).toTypedArray()
            val f = FloatArray(4)
            for (j in 0..3) {
                f[j] = x[j].toFloat()
            }
            pointList.add(f)
        }
        initWithPointList(pointList)
    }

    fun initWithPointList(pointList: ArrayList<FloatArray>) {
        var drawWidth = 0f
        var drawHeight = 0f
        val shouldLayout = mItemList.size > 0
        mItemList.clear()
        for (i in pointList.indices) {
            val line = pointList[i]
            val startPoint = PointF(dp2px(line[0]) * scale, dp2px(line[1]) * scale)
            val endPoint = PointF(dp2px(line[2]) * scale, dp2px(line[3]) * scale)
            drawWidth = drawWidth.coerceAtLeast(startPoint.x)
            drawWidth = drawWidth.coerceAtLeast(endPoint.x)
            drawHeight = drawHeight.coerceAtLeast(startPoint.y)
            drawHeight = drawHeight.coerceAtLeast(endPoint.y)
            val item = StoreHouseBarItem(i, startPoint, endPoint, mTextColor, mLineWidth)
            item.resetPosition(mHorizontalRandomness)
            mItemList.add(item)
        }
        mDrawZoneWidth = ceil(drawWidth.toDouble()).toInt()
        mDrawZoneHeight = ceil(drawHeight.toDouble()).toInt()
        if (shouldLayout) {
            requestLayout()
        }
    }

    private fun beginLoading() {
        mIsInLoading = true
        mAniController.start()
        invalidate()
    }

    private fun loadFinish() {
        mIsInLoading = false
        mAniController.stop()
    }

    @SuppressLint("DrawAllocation")
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val progress = mProgress
        val c1 = canvas.save()
        val len = mItemList.size
        for (i in 0 until len) {
            canvas.save()
            val storeHouseBarItem: StoreHouseBarItem = mItemList[i]
            var offsetX: Float = mOffsetX + storeHouseBarItem.midPoint.x
            var offsetY: Float = mOffsetY + storeHouseBarItem.midPoint.y
            if (mIsInLoading) {
                storeHouseBarItem.getTransformation(drawingTime, mTransformation)
                canvas.translate(offsetX, offsetY)
            } else {
                if (progress == 0f) {
                    storeHouseBarItem.resetPosition(mHorizontalRandomness)
                    continue
                }
                val startPadding = (1 - mInternalAnimationFactor) * i / len
                val endPadding = 1 - mInternalAnimationFactor - startPadding

                // done
                if (progress == 1f || progress >= 1 - endPadding) {
                    canvas.translate(offsetX, offsetY)
                    storeHouseBarItem.setAlpha(mBarDarkAlpha)
                } else {
                    val realProgress: Float = if (progress <= startPadding) 0f else 1f.coerceAtMost((progress - startPadding) / mInternalAnimationFactor)
                    offsetX += storeHouseBarItem.translationX * (1 - realProgress)
                    offsetY += -mDropHeight * (1 - realProgress)
                    val matrix = Matrix()
                    matrix.postRotate(360 * realProgress)
                    matrix.postScale(realProgress, realProgress)
                    matrix.postTranslate(offsetX, offsetY)
                    storeHouseBarItem.setAlpha(mBarDarkAlpha * realProgress)
                    canvas.concat(matrix)
                }
            }
            storeHouseBarItem.draw(canvas)
            canvas.restore()
        }
        if (mIsInLoading) {
            invalidate()
        }
        canvas.restoreToCount(c1)
    }

    override fun succeedRetention(): Long {
        return 200
    }

    override fun failingRetention(): Long {
        return 200
    }

    override fun refreshHeight(): Int {
        return height
    }

    override fun maxOffsetHeight(): Int {
        return 2 * height
    }

    override fun onReset(refreshLayout: RefreshLayout) {
        loadFinish()
        for (i in mItemList.indices) {
            mItemList[i].resetPosition(mHorizontalRandomness)
        }
    }

    override fun onPrepare(refreshLayout: RefreshLayout) {}

    override fun onRefresh(refreshLayout: RefreshLayout) {
        beginLoading()
    }

    override fun onComplete(refreshLayout: RefreshLayout, isSuccess: Boolean) {
        loadFinish()
    }

    override fun onScroll(refreshLayout: RefreshLayout, distance: Int, percent: Float, refreshing: Boolean) {
        val currentPercent = 1f.coerceAtMost(percent)
        setProgress(currentPercent)
        invalidate()
    }

    private inner class AniController : Runnable {
        private var mTick = 0
        private var mCountPerSeg = 0
        private var mSegCount = 0
        private var mInterval = 0
        private var mRunning = true

        fun start() {
            mRunning = true
            mTick = 0
            mInterval = mLoadingAniDuration / mItemList.size
            mCountPerSeg = mLoadingAniSegDuration / mInterval
            mSegCount = mItemList.size / mCountPerSeg + 1
            run()
        }

        override fun run() {
            val pos = mTick % mCountPerSeg
            for (i in 0 until mSegCount) {
                var index = i * mCountPerSeg + pos
                if (index > mTick) {
                    continue
                }
                index %= mItemList.size
                val item: StoreHouseBarItem = mItemList[index]
                item.fillAfter = false
                item.isFillEnabled = true
                item.fillBefore = false
                item.duration = mLoadingAniItemDuration
                item.start(mFromAlpha, mToAlpha)
            }
            mTick++
            if (mRunning) {
                postDelayed(this, mInterval.toLong())
            }
        }

        fun stop() {
            mRunning = false
            removeCallbacks(this)
        }
    }
}
package me.ibore.widget.wheel

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Cap
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.OverScroller
import androidx.annotation.*
import androidx.core.view.ViewCompat
import me.ibore.R
import me.ibore.ktx.dp2px
import me.ibore.ktx.sp2px
import me.ibore.widget.wheel.WheelView.DividerType.Companion.FILL
import me.ibore.widget.wheel.WheelView.DividerType.Companion.WRAP
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


@Suppress("DEPRECATION")
open class WheelView<T> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        View(context, attrs, defStyleAttr), Runnable {

    companion object {

        private const val DEFAULT_SCROLL_DURATION = 250

        private const val DEFAULT_CLICK_CONFIRM: Long = 120

        // 滚动状态
        const val SCROLL_STATE_IDLE = 0
        const val SCROLL_STATE_DRAGGING = 1
        const val SCROLL_STATE_SCROLLING = 2

    }

    /**
     * 自定义文字对齐方式注解
     */
    @IntDef(TextAlign.LEFT, TextAlign.CENTER, TextAlign.RIGHT)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class TextAlign {
        companion object {
            // 文字对齐方式
            const val LEFT = 0
            const val CENTER = 1
            const val RIGHT = 2
        }
    }

    /**
     * 自定义左右圆弧效果方向注解
     */
    @IntDef(CurvedArc.LEFT, CurvedArc.CENTER, CurvedArc.RIGHT)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class CurvedArc {
        companion object {
            // 弯曲效果对齐方式
            const val LEFT = 0
            const val CENTER = 1
            const val RIGHT = 2
        }
    }

    /**
     * 自定义分割线类型注解
     */
    @IntDef(FILL, WRAP)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class DividerType {
        companion object {
            // 分割线填充类型
            const val FILL = 0
            const val WRAP = 1
        }
    }

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // 数据列表
    open var datas: MutableList<T> = ArrayList()
        set(value) {
            field = value
            if (!resetSelectedPosition && datas.size > 0) { // 不重置选中下标
                if (selectedPosition >= datas.size) {
                    selectedPosition = datas.size - 1
                    // 重置滚动下标
                    currentScrollPosition = selectedPosition
                }
            } else { // 重置选中下标和滚动下标
                selectedPosition = 0
                currentScrollPosition = selectedPosition
            }
            // 强制滚动完成
            forceFinishScroll()
            calculateTextSize()
            calculateLimitY()
            // 重置滚动偏移
            mScrollOffsetY = selectedPosition * itemHeight
            requestLayout()
            invalidate()
        }

    // 字体大小
    var textSize = sp2px(15F)
        set(value) {
            field = value
            // 强制滚动完成
            forceFinishScroll()
            calculateTextSize()
            calculateDrawStart()
            calculateLimitY()
            // 字体大小变化，偏移距离也变化了
            mScrollOffsetY = selectedPosition * itemHeight
            requestLayout()
            invalidate()
        }

    // 是否自动调整字体大小以显示完全
    var autoFitTextSize = true
        set(value) {
            field = value
            invalidate()
        }

    private var mFontMetrics: Paint.FontMetrics? = null

    // 每个item的高度
    private var itemHeight = 0

    // 文字的最大宽度
    private var mMaxTextWidth = 0

    // 文字中心距离baseline的距离
    private var mCenterToBaselineY = 0

    // 可见的item条数
    var visibleItems = 7
        set(value) {
            // 跳转可见条目数为奇数
            field = abs(value / 2 * 2 + 1)
            mScrollOffsetY = 0
            requestLayout()
            invalidate()
        }

    // 每个item之间的空间，行间距
    var lineSpacing = dp2px(2F)
        set(value) {
            field = value
            mScrollOffsetY = 0
            calculateTextSize()
            requestLayout()
            invalidate()
        }

    // 是否循环滚动
    var cyclic = false
        set(value) {
            field = value
            forceFinishScroll()
            calculateLimitY()
            // 设置当前选中的偏移值
            mScrollOffsetY = selectedPosition * itemHeight
            invalidate()
        }

    // 文字对齐方式
    @TextAlign
    var textAlign = TextAlign.CENTER
        set(value) {
            if (textAlign == textAlign) {
                return
            }
            field = value
            updateTextAlign()
            calculateDrawStart()
            invalidate()

        }

    // 文字颜色
    var normalTextColor = Color.DKGRAY
        set(value) {
            field = value
            invalidate()
        }

    // 选中item文字颜色
    @ColorInt
    var selectedTextColor = 0
        set(value) {
            field = value
            invalidate()
        }

    // 是否显示分割线
    var showDivider = false
        set(value) {
            field = value
            invalidate()

        }

    // 分割线的颜色
    @ColorInt
    var dividerColor = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }

    // 分割线高度
    var dividerHeight = dp2px(1F)
        set(value) {
            field = value
            invalidate()
        }

    // 分割线填充类型
    @DividerType
    var dividerType = FILL
        set(value) {
            field = value
            invalidate()
        }

    // 分割线类型为DIVIDER_TYPE_WRAP时 分割线左右两端距离文字的间距
    var dividerPaddingForWrap = dp2px(2f).toFloat()
        set(value) {
            field = value
            invalidate()
        }

    // 分割线两端形状，默认圆头
    private var dividerCap = Cap.ROUND
        set(value) {
            field = value
            invalidate()
        }

    // 是否绘制选中区域
    var drawSelectedRect = false
        set(value) {
            field = value
            invalidate()
        }

    // 选中区域颜色
    var selectedRectColor = Color.TRANSPARENT
        set(value) {
            field = value
            invalidate()
        }

    // 文字起始X
    private var mStartX = 0

    // X轴中心点
    private var mCenterX = 0

    // Y轴中心点
    private var mCenterY = 0

    // 选中边界的上下限制
    private var mSelectedItemTopLimit = 0

    private var mSelectedItemBottomLimit = 0

    // 裁剪边界
    private var mClipLeft = 0

    private var mClipTop = 0

    private var mClipRight = 0

    private var mClipBottom = 0

    // 绘制区域
    private var mDrawRect: Rect? = null

    // 字体外边距，目的是留有边距
    var textBoundaryMargin = dp2px(2f)
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    // 数据为Integer类型时，是否需要格式转换
    var integerNeedFormat = false
        set(value) {
            field = value
            calculateTextSize()
            requestLayout()
            invalidate()
        }

    // 数据为Integer类型时，转换格式，默认转换为两位数
    var integerFormat: String = "%02d"
        set(value) {
            if (TextUtils.isEmpty(value) || field == value) {
                return
            }
            field = value
            calculateTextSize()
            requestLayout()
            invalidate()
        }

    // 3D效果
    private var mCamera: Camera? = null

    private var mMatrix: Matrix? = null

    // 是否是弯曲（3D）效果
    var curved: Boolean = true
        set(value) {
            field = value
            calculateTextSize()
            requestLayout()
            invalidate()
        }

    // 弯曲（3D）效果左右圆弧偏移效果方向 center 不偏移
    @CurvedArc
    var curvedArc: Int = CurvedArc.CENTER
        set(value) {
            field = value
            invalidate()
        }

    // 弯曲（3D）效果左右圆弧偏移效果系数 0-1之间 越大越明显
    @FloatRange(from = 0.0, to = 1.0)
    var curvedArcFactor: Float = 0.0f
        set(value) {
            field = when {
                value < 0 -> 0F
                value > 1 -> 1F
                else -> value
            }
            invalidate()
        }

    // 弯曲（3D）效果选中后折射的偏移 与字体大小的比值，1为不偏移 越小偏移越明显
    @FloatRange(from = 0.0, to = 1.0)
    var refractRatio = 0.9F
        set(value) {
            field = when {
                value < 0 -> 0.9F
                value > 1 -> 1F
                else -> value
            }
            invalidate()
        }

    // 字体
    var typeface: Typeface = Typeface.DEFAULT
        set(value) {
            field = value
            // 强制滚动完成
            forceFinishScroll()
            mPaint.typeface = typeface
            calculateTextSize()
            calculateDrawStart()
            // 字体大小变化，偏移距离也变化了
            mScrollOffsetY = selectedPosition * itemHeight
            calculateLimitY()
            requestLayout()
            invalidate()
        }

    // 数据变化时，是否重置选中下标到第一个位置
    var resetSelectedPosition = false

    private var mVelocityTracker: VelocityTracker? = null

    private var mMaxFlingVelocity = 0

    private var mMinFlingVelocity = 0

    private val mOverScroller: OverScroller = OverScroller(context)

    // 最小滚动距离，上边界
    private var mMinScrollY = 0

    // 最大滚动距离，下边界
    private var mMaxScrollY = 0

    // Y轴滚动偏移
    private var mScrollOffsetY = 0

    // Y轴已滚动偏移，控制重绘次数
    private var mScrolledY = 0

    // 手指最后触摸的位置
    private var mLastTouchY = 0f

    // 手指按下时间，根据按下抬起时间差处理点击滚动
    private var mDownStartTime: Long = 0

    // 是否强制停止滚动
    private var isForceFinishScroll = false

    // 是否是快速滚动，快速滚动结束后跳转位置
    private var isFlingScroll = false

    // 当前选中的下标
    var selectedPosition = 0
        private set

    // 当前滚动经过的下标
    private var currentScrollPosition = 0

    // 监听器
    var onSelectedListener: ((wheelView: WheelView<T>, data: T, position: Int) -> Unit)? = null
        set(value) {
            field = value
            setSelectedPosition(selectedPosition)
        }

    var onWheelChangedListener: OnWheelChangedListener? = null

    // 音频
    private var mSoundHelper: SoundHelper? = null

    // 是否开启音频效果
    var soundEffect = false

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelView)
        textSize = typedArray.getDimension(R.styleable.WheelView_wvTextSize, textSize.toFloat()).toInt()
        autoFitTextSize = typedArray.getBoolean(R.styleable.WheelView_wvAutoFitTextSize, autoFitTextSize)
        textAlign = typedArray.getInt(R.styleable.WheelView_wvTextAlign, TextAlign.CENTER)
        textBoundaryMargin = typedArray.getDimension(R.styleable.WheelView_wvTextBoundaryMargin, textBoundaryMargin.toFloat()).toInt()
        normalTextColor = typedArray.getColor(R.styleable.WheelView_wvNormalItemTextColor, normalTextColor)
        selectedTextColor = typedArray.getColor(R.styleable.WheelView_wvSelectedItemTextColor, selectedTextColor)
        lineSpacing = typedArray.getDimension(R.styleable.WheelView_wvLineSpacing, lineSpacing.toFloat()).toInt()
        integerNeedFormat = typedArray.getBoolean(R.styleable.WheelView_wvIntegerNeedFormat, integerNeedFormat)
        integerFormat = typedArray.getString(R.styleable.WheelView_wvIntegerFormat) ?: integerFormat

        visibleItems = typedArray.getInt(R.styleable.WheelView_wvVisibleItems, visibleItems)

        selectedPosition = typedArray.getInt(R.styleable.WheelView_wvSelectedItemPosition, 0)
        // 初始化滚动下标
        // 初始化滚动下标
        currentScrollPosition = selectedPosition
        cyclic = typedArray.getBoolean(R.styleable.WheelView_wvCyclic, false)

        showDivider = typedArray.getBoolean(R.styleable.WheelView_wvShowDivider, showDivider)
        dividerType = typedArray.getInt(R.styleable.WheelView_wvDividerType, dividerType)
        dividerHeight = typedArray.getDimension(R.styleable.WheelView_wvDividerHeight, dividerHeight.toFloat()).toInt()
        dividerColor = typedArray.getColor(R.styleable.WheelView_wvDividerColor, dividerColor)
        dividerPaddingForWrap = typedArray.getDimension(R.styleable.WheelView_wvDividerPaddingForWrap, dividerPaddingForWrap)

        drawSelectedRect = typedArray.getBoolean(R.styleable.WheelView_wvDrawSelectedRect, drawSelectedRect)
        selectedRectColor = typedArray.getColor(R.styleable.WheelView_wvSelectedRectColor, selectedRectColor)

        curved = typedArray.getBoolean(R.styleable.WheelView_wvCurved, curved)
        curvedArc = typedArray.getInt(R.styleable.WheelView_wvCurvedArc, curvedArc)
        curvedArcFactor = typedArray.getFloat(R.styleable.WheelView_wvCurvedArcFactor, curvedArcFactor)
        // 折射偏移默认值
        refractRatio = typedArray.getFloat(R.styleable.WheelView_wvRefractRatio, refractRatio)

        typedArray.recycle()

        val viewConfiguration = ViewConfiguration.get(context)
        mMaxFlingVelocity = viewConfiguration.scaledMaximumFlingVelocity
        mMinFlingVelocity = viewConfiguration.scaledMinimumFlingVelocity
        mDrawRect = Rect()
        mCamera = Camera()
        mMatrix = Matrix()
        if (!isInEditMode) {
            mSoundHelper = SoundHelper.obtain()
            initDefaultVolume(context)
        }
        calculateTextSize()
        updateTextAlign()

        if (isInEditMode) {
            //datas = arrayListOf("Item1", "Item2" , "Item3" ,"Item4", "Item5")
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mSoundHelper != null) {
            mSoundHelper!!.release()
        }
    }

    /**
     * 初始化默认音量
     *
     * @param context 上下文
     */
    private fun initDefaultVolume(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        if (audioManager != null) { // 获取系统媒体当前音量
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            // 获取系统媒体最大音量
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            // 设置播放音量
            mSoundHelper!!.playVolume = currentVolume * 1.0f / maxVolume
        } else {
            mSoundHelper!!.playVolume = 0.3f
        }
    }

    /**
     * 测量文字最大所占空间
     */
    private fun calculateTextSize() {
        mPaint.textSize = textSize.toFloat()
        for (i in datas.indices) {
            val textWidth = mPaint.measureText(getDataText(datas[i])).toInt()
            mMaxTextWidth = textWidth.coerceAtLeast(mMaxTextWidth)
        }
        mFontMetrics = mPaint.fontMetrics
        // itemHeight实际等于字体高度+一个行间距
        itemHeight = (mFontMetrics!!.bottom - mFontMetrics!!.top + lineSpacing).toInt()
    }

    /**
     * 更新textAlign
     */
    private fun updateTextAlign() {
        when (textAlign) {
            TextAlign.LEFT -> mPaint.textAlign = Paint.Align.LEFT
            TextAlign.RIGHT -> mPaint.textAlign = Paint.Align.RIGHT
            else -> mPaint.textAlign = Paint.Align.CENTER
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) { // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Line Space算在了mItemHeight中
        val height: Int = if (curved) {
            (itemHeight * visibleItems * 2 / Math.PI + paddingTop + paddingBottom).toInt()
        } else {
            itemHeight * visibleItems + paddingTop + paddingBottom
        }
        var width = (mMaxTextWidth + paddingLeft + paddingRight + textBoundaryMargin * 2)
        if (curved) {
            val towardRange = (sin(Math.PI / 48) * height).toInt()
            width += towardRange
        }
        setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0), resolveSizeAndState(height, heightMeasureSpec, 0))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 设置内容可绘制区域
        mDrawRect!![paddingLeft, paddingTop, width - paddingRight] = height - paddingBottom
        mCenterX = mDrawRect!!.centerX()
        mCenterY = mDrawRect!!.centerY()
        mSelectedItemTopLimit = mCenterY - itemHeight / 2
        mSelectedItemBottomLimit = mCenterY + itemHeight / 2
        mClipLeft = paddingLeft
        mClipTop = paddingTop
        mClipRight = width - paddingRight
        mClipBottom = height - paddingBottom
        calculateDrawStart()
        // 计算滚动限制
        calculateLimitY()
    }

    /**
     * 起算起始位置
     */
    private fun calculateDrawStart() {
        mStartX = when (textAlign) {
            TextAlign.LEFT -> (paddingLeft + textBoundaryMargin)
            TextAlign.RIGHT -> (width - paddingRight - textBoundaryMargin)
            TextAlign.CENTER -> width / 2
            else -> width / 2
        }
        // 文字中心距离baseline的距离
        mCenterToBaselineY = (mFontMetrics!!.ascent + (mFontMetrics!!.descent - mFontMetrics!!.ascent) / 2).toInt()
    }

    /**
     * 计算滚动限制
     */
    private fun calculateLimitY() {
        mMinScrollY = if (cyclic) Int.MIN_VALUE else 0
        // 下边界 (dataSize - 1 - mInitPosition) * mItemHeight
        mMaxScrollY = if (cyclic) Int.MAX_VALUE else (datas.size - 1) * itemHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 绘制选中区域
        drawSelectedRect(canvas)
        // 绘制分割线
        drawDivider(canvas)
        // 滚动了多少个item，滚动的Y值高度除去每行Item的高度
        val scrolledItem = mScrollOffsetY / itemHeight
        // 没有滚动完一个item时的偏移值，平滑滑动
        val scrolledOffset = mScrollOffsetY % itemHeight
        // 向上取整
        val halfItem = (visibleItems + 1) / 2
        // 计算的最小index
        val minIndex: Int
        // 计算的最大index
        val maxIndex: Int
        if (scrolledOffset < 0) { // 小于0
            minIndex = scrolledItem - halfItem - 1
            maxIndex = scrolledItem + halfItem
        } else if (scrolledOffset > 0) {
            minIndex = scrolledItem - halfItem
            maxIndex = scrolledItem + halfItem + 1
        } else {
            minIndex = scrolledItem - halfItem
            maxIndex = scrolledItem + halfItem
        }
        // 绘制item
        for (i in minIndex until maxIndex) {
            if (curved) {
                draw3DItem(canvas, i, scrolledOffset)
            } else {
                drawItem(canvas, i, scrolledOffset)
            }
        }
    }

    /**
     * 绘制选中区域
     *
     * @param canvas 画布
     */
    private fun drawSelectedRect(canvas: Canvas) {
        if (drawSelectedRect) {
            mPaint.color = selectedRectColor
            canvas.drawRect(mClipLeft.toFloat(), mSelectedItemTopLimit.toFloat(), mClipRight.toFloat(), mSelectedItemBottomLimit.toFloat(), mPaint)
        }
    }

    /**
     * 绘制分割线
     *
     * @param canvas 画布
     */
    private fun drawDivider(canvas: Canvas) {
        if (showDivider) {
            mPaint.color = dividerColor
            val originStrokeWidth = mPaint.strokeWidth
            mPaint.strokeJoin = Paint.Join.ROUND
            mPaint.strokeCap = Cap.ROUND
            mPaint.strokeWidth = dividerHeight.toFloat()
            if (dividerType == FILL) {
                canvas.drawLine(mClipLeft.toFloat(), mSelectedItemTopLimit.toFloat(), mClipRight.toFloat(), mSelectedItemTopLimit.toFloat(), mPaint)
                canvas.drawLine(mClipLeft.toFloat(), mSelectedItemBottomLimit.toFloat(), mClipRight.toFloat(), mSelectedItemBottomLimit.toFloat(), mPaint)
            } else { // 边界处理 超过边界直接按照DIVIDER_TYPE_FILL类型处理
                val startX = (mCenterX - mMaxTextWidth / 2 - dividerPaddingForWrap).toInt()
                val stopX = (mCenterX + mMaxTextWidth / 2 + dividerPaddingForWrap).toInt()
                val wrapStartX = if (startX < mClipLeft) mClipLeft else startX
                val wrapStopX = if (stopX > mClipRight) mClipRight else stopX
                canvas.drawLine(wrapStartX.toFloat(), mSelectedItemTopLimit.toFloat(), wrapStopX.toFloat(), mSelectedItemTopLimit.toFloat(), mPaint)
                canvas.drawLine(wrapStartX.toFloat(), mSelectedItemBottomLimit.toFloat(), wrapStopX.toFloat(), mSelectedItemBottomLimit.toFloat(), mPaint)
            }
            mPaint.strokeWidth = originStrokeWidth
        }
    }

    /**
     * 绘制2D效果
     *
     * @param canvas         画布
     * @param index          下标
     * @param scrolledOffset 滚动偏移
     */
    private fun drawItem(canvas: Canvas, index: Int, scrolledOffset: Int) {
        val text = getDataByIndex(index) ?: return
        // index 的 item 距离中间项的偏移
        val item2CenterOffsetY = (index - mScrollOffsetY / itemHeight) * itemHeight - scrolledOffset
        // 记录初始测量的字体起始X
        val startX = mStartX
        // 重新测量字体宽度和基线偏移
        val centerToBaselineY = if (autoFitTextSize) remeasureTextSize(text) else mCenterToBaselineY
        if (abs(item2CenterOffsetY) <= 0) { // 绘制选中的条目
            mPaint.color = selectedTextColor
            clipAndDraw2DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY)
        } else if (item2CenterOffsetY in 1 until itemHeight) { // 绘制与下边界交汇的条目
            mPaint.color = selectedTextColor
            clipAndDraw2DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY)
            mPaint.color = normalTextColor
            clipAndDraw2DText(canvas, text, mSelectedItemBottomLimit, mClipBottom, item2CenterOffsetY, centerToBaselineY)
        } else if (item2CenterOffsetY < 0 && item2CenterOffsetY > -itemHeight) { // 绘制与上边界交汇的条目
            mPaint.color = selectedTextColor
            clipAndDraw2DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY)
            mPaint.color = normalTextColor
            clipAndDraw2DText(canvas, text, mClipTop, mSelectedItemTopLimit, item2CenterOffsetY, centerToBaselineY)
        } else { // 绘制其他条目
            mPaint.color = normalTextColor
            clipAndDraw2DText(canvas, text, mClipTop, mClipBottom, item2CenterOffsetY, centerToBaselineY)
        }
        if (autoFitTextSize) { // 恢复重新测量之前的样式
            mPaint.textSize = textSize.toFloat()
            mStartX = startX
        }
    }

    /**
     * 裁剪并绘制2d text
     *
     * @param canvas             画布
     * @param text               绘制的文字
     * @param clipTop            裁剪的上边界
     * @param clipBottom         裁剪的下边界
     * @param item2CenterOffsetY 距离中间项的偏移
     * @param centerToBaselineY  文字中心距离baseline的距离
     */
    private fun clipAndDraw2DText(canvas: Canvas, text: String, clipTop: Int, clipBottom: Int, item2CenterOffsetY: Int, centerToBaselineY: Int) {
        canvas.save()
        canvas.clipRect(mClipLeft, clipTop, mClipRight, clipBottom)
        canvas.drawText(text, 0, text.length, mStartX.toFloat(), mCenterY + item2CenterOffsetY - centerToBaselineY.toFloat(), mPaint)
        canvas.restore()
    }

    /**
     * 重新测量字体大小
     *
     * @param contentText 被测量文字内容
     * @return 文字中心距离baseline的距离
     */
    private fun remeasureTextSize(contentText: String): Int {
        var textWidth = mPaint.measureText(contentText)
        var drawWidth = width.toFloat()
        var textMargin = textBoundaryMargin * 2
        // 稍微增加了一点文字边距 最大为宽度的1/10
        if (textMargin > drawWidth / 10f) {
            drawWidth = drawWidth * 9f / 10f
            textMargin = (drawWidth / 10f).toInt()
        } else {
            drawWidth -= textMargin
        }
        if (drawWidth <= 0) {
            return mCenterToBaselineY
        }
        var textSize = textSize
        while (textWidth > drawWidth) {
            textSize--
            if (textSize <= 0) {
                break
            }
            mPaint.textSize = textSize.toFloat()
            textWidth = mPaint.measureText(contentText)
        }
        // 重新计算文字起始X
        recalculateStartX(textMargin / 2.0f)
        // 高度起点也变了
        return recalculateCenterToBaselineY()
    }

    /**
     * 重新计算字体起始X
     *
     * @param textMargin 文字外边距
     */
    private fun recalculateStartX(textMargin: Float) {
        mStartX = when (textAlign) {
            TextAlign.LEFT -> textMargin.toInt()
            TextAlign.RIGHT -> (width - textMargin).toInt()
            else -> width / 2
        }
    }

    /**
     * 字体大小变化后重新计算距离基线的距离
     *
     * @return 文字中心距离baseline的距离
     */
    private fun recalculateCenterToBaselineY(): Int {
        val fontMetrics = mPaint.fontMetrics
        // 高度起点也变了
        return (fontMetrics.ascent + (fontMetrics.descent - fontMetrics.ascent) / 2).toInt()
    }

    /**
     * 绘制弯曲（3D）效果的item
     *
     * @param canvas         画布
     * @param index          下标
     * @param scrolledOffset 滚动偏移
     */
    private fun draw3DItem(canvas: Canvas, index: Int, scrolledOffset: Int) {
        val text = getDataByIndex(index) ?: return
        // 滚轮的半径
        val radius = (height - paddingTop - paddingBottom) / 2
        // index 的 item 距离中间项的偏移
        val item2CenterOffsetY = (index - mScrollOffsetY / itemHeight) * itemHeight - scrolledOffset
        // 当滑动的角度和y轴垂直时（此时文字已经显示为一条线），不绘制文字
        if (abs(item2CenterOffsetY) > radius * Math.PI / 2) return
        val angle = item2CenterOffsetY.toDouble() / radius
        // 绕x轴滚动的角度
        val rotateX = Math.toDegrees(-angle).toFloat()
        // 滚动的距离映射到y轴的长度
        val translateY = (sin(angle) * radius).toFloat()
        // 滚动的距离映射到z轴的长度
        val translateZ = ((1 - cos(angle)) * radius).toFloat()
        // 透明度
        val alpha = (cos(angle) * 255).toInt()
        // 记录初始测量的字体起始X
        val startX = mStartX
        // 重新测量字体宽度和基线偏移
        val centerToBaselineY = if (autoFitTextSize) remeasureTextSize(text) else mCenterToBaselineY
        if (abs(item2CenterOffsetY) <= 0) { // 绘制选中的条目
            mPaint.color = selectedTextColor
            mPaint.alpha = 255
            clipAndDraw3DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, rotateX, translateY, translateZ, centerToBaselineY)
        } else if (item2CenterOffsetY in 1 until itemHeight) { // 绘制与下边界交汇的条目
            mPaint.color = selectedTextColor
            mPaint.alpha = 255
            clipAndDraw3DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, rotateX, translateY, translateZ, centerToBaselineY)
            mPaint.color = normalTextColor
            mPaint.alpha = alpha
            // 缩小字体，实现折射效果
            val textSize = mPaint.textSize
            mPaint.textSize = textSize * refractRatio
            // 字体变化，重新计算距离基线偏移
            val reCenterToBaselineY = recalculateCenterToBaselineY()
            clipAndDraw3DText(canvas, text, mSelectedItemBottomLimit, mClipBottom, rotateX, translateY, translateZ, reCenterToBaselineY)
            mPaint.textSize = textSize
        } else if (item2CenterOffsetY < 0 && item2CenterOffsetY > -itemHeight) { // 绘制与上边界交汇的条目
            mPaint.color = selectedTextColor
            mPaint.alpha = 255
            clipAndDraw3DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, rotateX, translateY, translateZ, centerToBaselineY)
            mPaint.color = normalTextColor
            mPaint.alpha = alpha
            // 缩小字体，实现折射效果
            val textSize = mPaint.textSize
            mPaint.textSize = textSize * refractRatio
            // 字体变化，重新计算距离基线偏移
            val reCenterToBaselineY = recalculateCenterToBaselineY()
            clipAndDraw3DText(canvas, text, mClipTop, mSelectedItemTopLimit, rotateX, translateY, translateZ, reCenterToBaselineY)
            mPaint.textSize = textSize
        } else { // 绘制其他条目
            mPaint.color = normalTextColor
            mPaint.alpha = alpha
            // 缩小字体，实现折射效果
            val textSize = mPaint.textSize
            mPaint.textSize = textSize * refractRatio
            // 字体变化，重新计算距离基线偏移
            val reCenterToBaselineY = recalculateCenterToBaselineY()
            clipAndDraw3DText(canvas, text, mClipTop, mClipBottom, rotateX, translateY, translateZ, reCenterToBaselineY)
            mPaint.textSize = textSize
        }
        if (autoFitTextSize) { // 恢复重新测量之前的样式
            mPaint.textSize = textSize.toFloat()
            mStartX = startX
        }
    }

    /**
     * 裁剪并绘制弯曲（3D）效果
     *
     * @param canvas            画布
     * @param text              绘制的文字
     * @param clipTop           裁剪的上边界
     * @param clipBottom        裁剪的下边界
     * @param rotateX           绕X轴旋转角度
     * @param offsetY           Y轴偏移
     * @param offsetZ           Z轴偏移
     * @param centerToBaselineY 文字中心距离baseline的距离
     */
    private fun clipAndDraw3DText(canvas: Canvas, text: String, clipTop: Int, clipBottom: Int, rotateX: Float, offsetY: Float, offsetZ: Float, centerToBaselineY: Int) {
        canvas.save()
        canvas.clipRect(mClipLeft, clipTop, mClipRight, clipBottom)
        draw3DText(canvas, text, rotateX, offsetY, offsetZ, centerToBaselineY)
        canvas.restore()
    }

    /**
     * 绘制弯曲（3D）的文字
     *
     * @param canvas            画布
     * @param text              绘制的文字
     * @param rotateX           绕X轴旋转角度
     * @param offsetY           Y轴偏移
     * @param offsetZ           Z轴偏移
     * @param centerToBaselineY 文字中心距离baseline的距离
     */
    private fun draw3DText(canvas: Canvas, text: String, rotateX: Float, offsetY: Float, offsetZ: Float, centerToBaselineY: Int) {
        mCamera!!.save()
        mCamera!!.translate(0f, 0f, offsetZ)
        mCamera!!.rotateX(rotateX)
        mCamera!!.getMatrix(mMatrix)
        mCamera!!.restore()
        // 调节中心点
        var centerX = mCenterX.toFloat()
        // 根据弯曲（3d）对齐方式设置系数
        if (curvedArc == CurvedArc.LEFT) {
            centerX = mCenterX * (1 + curvedArcFactor)
        } else if (curvedArc == CurvedArc.RIGHT) {
            centerX = mCenterX * (1 - curvedArcFactor)
        }
        val centerY = mCenterY + offsetY
        mMatrix!!.preTranslate(-centerX, -centerY)
        mMatrix!!.postTranslate(centerX, centerY)
        canvas.concat(mMatrix)
        canvas.drawText(text, 0, text.length, mStartX.toFloat(), centerY - centerToBaselineY, mPaint)
    }

    /**
     * 根据下标获取到内容
     *
     * @param index 下标
     * @return 绘制的文字内容
     */
    private fun getDataByIndex(index: Int): String? {
        val dataSize = datas.size
        if (dataSize == 0) {
            return null
        }
        var itemText: String? = null
        if (cyclic) {
            var i = index % dataSize
            if (i < 0) {
                i += dataSize
            }
            itemText = getDataText(datas[i])
        } else {
            if (index in 0 until dataSize) {
                itemText = getDataText(datas[index])
            }
        }
        return itemText
    }

    /**
     * 获取item text
     *
     * @param item item数据
     * @return 文本内容
     */
    protected open fun getDataText(item: T?): String? {
        return when (item) {
            null -> ""
            is IWheelEntity -> {
                (item as IWheelEntity).wheelText
            }
            is Int -> { // 如果为整形则最少保留两位数.
                if (integerNeedFormat) String.format(integerFormat!!, item) else item.toString()
            }
            is String -> item
            else -> item.toString()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (datas.isNullOrEmpty()) return false
        initVelocityTracker()
        mVelocityTracker!!.addMovement(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // 手指按下
                // 处理滑动事件嵌套 拦截事件序列
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                // 如果未滚动完成，强制滚动完成
                if (!mOverScroller.isFinished) { // 强制滚动完成
                    mOverScroller.forceFinished(true)
                    isForceFinishScroll = true
                }
                mLastTouchY = event.y
                // 按下时间
                mDownStartTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_MOVE -> {
                // 手指移动
                val moveY = event.y
                val deltaY = moveY - mLastTouchY
                onWheelChangedListener?.onWheelScrollStateChanged(SCROLL_STATE_DRAGGING)
                if (abs(deltaY) < 1) {
                    return true
                }
                // deltaY 上滑为正，下滑为负
                doScroll((-deltaY).toInt())
                mLastTouchY = moveY
                invalidateIfYChanged()
            }
            MotionEvent.ACTION_UP -> {
                // 手指抬起
                isForceFinishScroll = false
                mVelocityTracker!!.computeCurrentVelocity(1000, mMaxFlingVelocity.toFloat())
                val velocityY = mVelocityTracker!!.yVelocity
                if (abs(velocityY) > mMinFlingVelocity) { // 快速滑动
                    mOverScroller.forceFinished(true)
                    isFlingScroll = true
                    mOverScroller.fling(0, mScrollOffsetY, 0, (-velocityY).toInt(), 0, 0, mMinScrollY, mMaxScrollY)
                } else {
                    var clickToCenterDistance = 0
                    if (System.currentTimeMillis() - mDownStartTime <= DEFAULT_CLICK_CONFIRM) { // 处理点击滚动
                        // 手指抬起的位置到中心的距离为滚动差值
                        clickToCenterDistance = (event.y - mCenterY).toInt()
                    }
                    val scrollRange = clickToCenterDistance + calculateDistanceToEndPoint((mScrollOffsetY + clickToCenterDistance) % itemHeight)
                    // 平稳滑动
                    mOverScroller.startScroll(0, mScrollOffsetY, 0, scrollRange)
                }
                invalidateIfYChanged()
                ViewCompat.postOnAnimation(this, this)
                // 回收 VelocityTracker
                recycleVelocityTracker()
            }
            MotionEvent.ACTION_CANCEL ->  // 事件被终止
                // 回收
                recycleVelocityTracker()
        }
        return true
    }

    /**
     * 初始化 VelocityTracker
     */
    private fun initVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    /**
     * 回收 VelocityTracker
     */
    private fun recycleVelocityTracker() {
        mVelocityTracker?.recycle()
        mVelocityTracker = null
    }

    /**
     * 计算滚动偏移
     *
     * @param distance 滚动距离
     */
    private fun doScroll(distance: Int) {
        mScrollOffsetY += distance
        if (!cyclic) { // 修正边界
            if (mScrollOffsetY < mMinScrollY) {
                mScrollOffsetY = mMinScrollY
            } else if (mScrollOffsetY > mMaxScrollY) {
                mScrollOffsetY = mMaxScrollY
            }
        }
    }

    /**
     * 当Y轴的偏移值改变时再重绘，减少重回次数
     */
    private fun invalidateIfYChanged() {
        if (mScrollOffsetY != mScrolledY) {
            mScrolledY = mScrollOffsetY
            // 滚动偏移发生变化
            onWheelChangedListener?.onWheelScroll(mScrollOffsetY)
            // 观察item变化
            observeItemChanged()
            invalidate()
        }
        selectedPosition = currentScrollPosition
        onSelectedListener?.invoke(this, datas[currentScrollPosition], currentScrollPosition)
    }

    /**
     * 观察item改变
     */
    private fun observeItemChanged() { // item改变回调
        val oldPosition = currentScrollPosition
        val newPosition = getCurrentPosition()
        if (oldPosition != newPosition) { // 改变了
            onWheelChangedListener?.onWheelItemChanged(oldPosition, newPosition)
            // 播放音频
            playSoundEffect()
            // 更新下标
            currentScrollPosition = newPosition
        }
    }

    /**
     * 播放滚动音效
     */
    open fun playSoundEffect() {
        if (mSoundHelper != null && soundEffect) {
            mSoundHelper?.playSoundEffect()
        }
    }

    /**
     * 强制滚动完成，直接停止
     */
    open fun forceFinishScroll() {
        if (!mOverScroller.isFinished) {
            mOverScroller.forceFinished(true)
        }
    }

    /**
     * 强制滚动完成，并且直接滚动到最终位置
     */
    open fun abortFinishScroll() {
        if (!mOverScroller.isFinished) {
            mOverScroller.abortAnimation()
        }
    }

    /**
     * 计算距离终点的偏移，修正选中条目
     *
     * @param remainder 余数
     * @return 偏移量
     */
    private fun calculateDistanceToEndPoint(remainder: Int): Int {
        return if (abs(remainder) > itemHeight / 2) {
            if (mScrollOffsetY < 0) {
                -itemHeight - remainder
            } else {
                itemHeight - remainder
            }
        } else {
            -remainder
        }
    }

    /**
     * 使用run方法而不是computeScroll是因为，invalidate也会执行computeScroll导致回调执行不准确
     */
    override fun run() { // 停止滚动更新当前下标
        if (mOverScroller.isFinished && !isForceFinishScroll && !isFlingScroll) {
            if (itemHeight == 0) return
            // 滚动状态停止
            onWheelChangedListener?.onWheelScrollStateChanged(SCROLL_STATE_IDLE)
            val currentItemPosition = getCurrentPosition()
            // 当前选中的Position没变时不回调 onItemSelected()
            if (currentItemPosition == selectedPosition) {
                return
            }
            selectedPosition = currentItemPosition
            // 停止后重新赋值
            currentScrollPosition = selectedPosition
            // 停止滚动，选中条目回调
            onSelectedListener?.invoke(this, datas[selectedPosition], selectedPosition)
            // 滚动状态回调
            onWheelChangedListener?.onWheelSelected(selectedPosition)
        }
        if (mOverScroller.computeScrollOffset()) {
            val oldY = mScrollOffsetY
            mScrollOffsetY = mOverScroller.currY
            if (oldY != mScrollOffsetY) {
                onWheelChangedListener?.onWheelScrollStateChanged(SCROLL_STATE_SCROLLING)
            }
            invalidateIfYChanged()
            ViewCompat.postOnAnimation(this, this)
        } else if (isFlingScroll) { // 滚动完成后，根据是否为快速滚动处理是否需要调整最终位置
            isFlingScroll = false
            // 快速滚动后需要调整滚动完成后的最终位置，重新启动scroll滑动到中心位置
            mOverScroller.startScroll(0, mScrollOffsetY, 0, calculateDistanceToEndPoint(mScrollOffsetY % itemHeight))
            invalidateIfYChanged()
            ViewCompat.postOnAnimation(this, this)
        }
    }

    /**
     * 根据偏移计算当前位置下标
     *
     * @return 偏移量对应的当前下标
     */
    private fun getCurrentPosition(): Int {
        val itemPosition: Int = if (mScrollOffsetY < 0) {
            (mScrollOffsetY - itemHeight / 2) / itemHeight
        } else {
            (mScrollOffsetY + itemHeight / 2) / itemHeight
        }
        var currentPosition = itemPosition % datas.size
        if (currentPosition < 0) {
            currentPosition += datas.size
        }
        return currentPosition
    }

    // 声音效果资源
    @RawRes
    var soundEffectResource: Int = 0
        set(value) {
            field = value
            mSoundHelper?.load(context, field)
        }

    // 获取播放音量 range 0.0-1.0
    @FloatRange(from = 0.0, to = 1.0)
    var playVolume: Float = 0F
        set(value) {
            field = when {
                value < 0 -> 0.0F
                value > 1 -> 1F
                else -> value
            }
            mSoundHelper?.playVolume = playVolume
        }

    /**
     * 获取指定 position 的数据
     *
     * @param position 下标
     * @return position 对应的数据 [Nullable]
     */
    fun getData(position: Int): T? {
        if (isPositionInRange(position)) {
            return datas[position]
        } else if (datas.size in 1..position) {
            return datas[datas.size - 1]
        } else if (datas.size > 0 && position < 0) {
            return datas[0]
        }
        return null
    }

    // 当前选中的item数据
    val selectedData: T? get() = getData(selectedPosition)

    /**
     * 设置当前选中下标
     *
     * @param position       下标
     * @param smoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动时间
     */
    open fun setSelectedPosition(position: Int, smoothScroll: Boolean = false, smoothDuration: Int = 0) {
        if (!isPositionInRange(position)) {
            return
        }
        // item之间差值
        val itemDistance = position * itemHeight - mScrollOffsetY
        // 如果Scroller滑动未停止，强制结束动画
        abortFinishScroll()
        if (smoothScroll) { // 如果是平滑滚动并且之前的Scroll滚动完成
            mOverScroller.startScroll(0, mScrollOffsetY, 0, itemDistance, if (smoothDuration > 0) smoothDuration else DEFAULT_SCROLL_DURATION)
            invalidateIfYChanged()
            ViewCompat.postOnAnimation(this, this)
        } else {
            doScroll(itemDistance)
            selectedPosition = position
            // 选中条目回调
            onSelectedListener?.invoke(this, datas[selectedPosition], selectedPosition)
            onWheelChangedListener?.onWheelSelected(selectedPosition)
            invalidateIfYChanged()
        }
    }

    /**
     * 判断下标是否在数据列表范围内
     *
     * @param position 下标
     * @return 是否在数据列表范围内
     */
    private fun isPositionInRange(position: Int): Boolean {
        return position >= 0 && position < datas.size
    }

    /**
     * WheelView滚动状态改变监听器
     */
    interface OnWheelChangedListener {
        /**
         * WheelView 滚动
         *
         * @param scrollOffsetY 滚动偏移
         */
        fun onWheelScroll(scrollOffsetY: Int)

        /**
         * WheelView 条目变化
         *
         * @param oldPosition 旧的下标
         * @param newPosition 新下标
         */
        fun onWheelItemChanged(oldPosition: Int, newPosition: Int)

        /**
         * WheelView 选中
         *
         * @param position 选中的下标
         */
        fun onWheelSelected(position: Int)

        /**
         * WheelView 滚动状态
         *
         * @param state 滚动状态 [SCROLL_STATE_IDLE]
         * [SCROLL_STATE_DRAGGING]
         * [SCROLL_STATE_SCROLLING]
         */
        fun onWheelScrollStateChanged(state: Int)
    }

    /**
     * SoundPool 辅助类
     */
    private class SoundHelper private constructor() {

        private var mSoundPool: SoundPool? = null
        private var mSoundId = 0

        init {
            mSoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                SoundPool.Builder().build()
            } else {
                SoundPool(1, AudioManager.STREAM_SYSTEM, 1)
            }
        }

        // 音量
        @FloatRange(from = 0.0, to = 1.0)
        var playVolume = 0f

        /**
         * 加载音频资源
         *
         * @param context 上下文
         * @param resId   音频资源 [RawRes]
         */
        fun load(context: Context?, @RawRes resId: Int) {
            mSoundId = mSoundPool?.load(context, resId, 1) ?: 0
        }

        /**
         * 播放声音效果
         */
        fun playSoundEffect() {
            if (mSoundPool != null && mSoundId != 0) {
                mSoundPool!!.play(mSoundId, playVolume, playVolume, 1, 0, 1f)
            }
        }

        /**
         * 释放SoundPool
         */
        fun release() {
            if (mSoundPool != null) {
                mSoundPool!!.release()
                mSoundPool = null
            }
        }

        companion object {
            /**
             * 初始化 SoundHelper
             *
             * @return SoundHelper 对象
             */
            fun obtain(): SoundHelper {
                return SoundHelper()
            }
        }

    }

    interface IWheelEntity {
        val wheelText: String
    }

}
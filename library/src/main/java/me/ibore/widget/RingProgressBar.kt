package me.ibore.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import me.ibore.R
import me.ibore.ktx.dp2px


class RingProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {

    companion object {
        //空心样式
        const val STROKE = 0

        //实心样式
        const val FILL = 1
    }

    //画笔对象
    private val paint: Paint = Paint()

    //View宽度
    private var viewWidth = 0

    //View高度
    private var viewHeight = 0

    //默认宽高值
    private var result = 0

    //默认padding值
    private var padding = 0f

    //圆环的颜色
    var ringColor: Int

    //圆环进度颜色
    var ringProgressColor: Int

    //文字颜色
    var textColor: Int

    //文字大小
    var textSize: Float

    //文字大小
    var textFormat: String

    //圆环宽度
    var ringWidth: Float

    //最大值
    private var max: Int

    //进度值
    private var progress: Int

    //是否显示文字
    private val textIsShow: Boolean

    //圆环进度条的样式
    private val style: Int

    //进度回调接口
    private var mOnProgressListener: OnProgressListener? = null

    //进度回调接口
    var onFormatListener: OnFormatListener? = null

    // 圆环中心
    private var centre = 0

    // 圆环半径
    private var radius = 0

    init {
        //初始化默认宽高值
        result = dp2px(100F)
        //初始化属性
        val mTypedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RingProgressBar)
        ringColor = mTypedArray.getColor(R.styleable.RingProgressBar_rpbRingColor, Color.BLACK)
        ringProgressColor = mTypedArray.getColor(R.styleable.RingProgressBar_rpbRingProgressColor, Color.WHITE)
        textColor = mTypedArray.getColor(R.styleable.RingProgressBar_rpbTextColor, Color.BLACK)
        textSize = mTypedArray.getDimension(R.styleable.RingProgressBar_rpbTextSize, 16f)
        textFormat = mTypedArray.getString(R.styleable.RingProgressBar_rpbTextFormat) ?: "%d"
        ringWidth = mTypedArray.getDimension(R.styleable.RingProgressBar_rpbRingWidth, 5f)
        max = mTypedArray.getInteger(R.styleable.RingProgressBar_rpbMax, 100)
        textIsShow = mTypedArray.getBoolean(R.styleable.RingProgressBar_rpbTextIsShow, true)
        style = mTypedArray.getInt(R.styleable.RingProgressBar_rpbStyle, 0)
        progress = mTypedArray.getInteger(R.styleable.RingProgressBar_rpbProgress, 0)
        padding = mTypedArray.getDimension(R.styleable.RingProgressBar_rpbRingPadding, 5f)
        mTypedArray.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        centre = width / 2
        radius = (centre - ringWidth / 2).toInt()
        //绘制外层圆
        drawCircle(canvas)
        //绘制文本内容
        drawTextContent(canvas)
        //绘制进度条
        drawProgress(canvas)
    }

    /**
     * 绘制外层圆环
     */
    private fun drawCircle(canvas: Canvas) { //设置画笔颜色
        paint.color = ringColor
        //设置画笔样式
        paint.style = Paint.Style.STROKE
        //设置stroke的宽度
        paint.strokeWidth = ringWidth
        //设置抗锯齿
        paint.isAntiAlias = true
        //绘制圆形
        canvas.drawCircle(centre.toFloat(), centre.toFloat(), radius.toFloat(), paint)
    }

    /**
     * 绘制进度文本
     */
    private fun drawTextContent(canvas: Canvas) { //设置stroke的宽度
        //设置文字的颜色
        paint.color = textColor
        paint.strokeWidth = 0F
        paint.style = Paint.Style.FILL
        //设置文字的大小
        paint.textSize = textSize
        //设置文字的style
        paint.typeface = Typeface.DEFAULT
        //绘制文本 会根据设置的是否显示文本的属性&是否是Stroke的样式进行判断
        if (textIsShow && style == STROKE) {
            //获取文字的宽度 用于绘制文本内容
            val centerText = onFormatListener?.format(progress)
                    ?: String.format(textFormat, progress)
            val textWidth: Float = paint.measureText(centerText)
            val fontMetrics: Paint.FontMetrics = paint.fontMetrics
            val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
            val baseline: Float = centre + distance
            canvas.drawText(centerText, centre - textWidth / 2, baseline, paint)
        }
    }

    /**
     * 绘制进度条
     */
    private fun drawProgress(canvas: Canvas) { //绘制进度 根据设置的样式进行绘制
        paint.strokeWidth = ringWidth
        paint.color = ringProgressColor
        //Stroke样式
        val strokeOval = RectF((centre - radius).toFloat(), (centre - radius).toFloat(), (centre + radius).toFloat(),
                (centre + radius).toFloat())
        //FIll样式
        val fillOval = RectF(centre - radius + ringWidth + padding,
                centre - radius + ringWidth + padding, centre + radius - ringWidth - padding,
                centre + radius - ringWidth - padding)
        when (style) {
            STROKE -> {
                paint.style = Paint.Style.STROKE
                paint.strokeCap = Paint.Cap.ROUND
                canvas.drawArc(strokeOval, -90F, 360F * progress / max, false, paint)
            }
            FILL -> {
                paint.style = Paint.Style.FILL_AND_STROKE
                paint.strokeCap = Paint.Cap.ROUND
                if (progress != 0) {
                    canvas.drawArc(fillOval, -90F, 360F * progress / max, true, paint)
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获取宽高的mode和size
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        //测量宽度
        viewWidth = if (widthMode == MeasureSpec.AT_MOST) result else widthSize
        //测量高度
        viewHeight = if (heightMode == MeasureSpec.AT_MOST) result else heightSize
        //设置测量的宽高值
        setMeasuredDimension(viewWidth, viewHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //确定View的宽高
        viewWidth = w
        viewHeight = h
    }

    /**
     * 获取当前的最大进度值
     */
    @Synchronized
    fun getMax(): Int {
        return max
    }

    /**
     * 设置最大进度值
     */
    @Synchronized
    fun setMax(max: Int) {
        require(max >= 0) { "The max progress of 0" }
        this.max = max
    }

    /**
     * 获取进度值
     */
    @Synchronized
    fun getProgress(): Int {
        return progress
    }

    /**
     * 设置进度值 根据进度值进行View的重绘刷新进度
     */
    @Synchronized
    fun setProgress(progress: Int) {
        require(progress >= 0) { "The progress of 0" }
        if (progress > max) {
            this.progress = max
        }
        if (progress <= max) {
            this.progress = progress
            postInvalidate()
        }
        if (progress == max) {
            mOnProgressListener?.progressToComplete()
        }
    }

    /**
     * 进度完成回调接口
     */
    interface OnProgressListener {
        fun progressToComplete()
    }

    fun setOnProgressListener(mOnProgressListener: OnProgressListener?) {
        this.mOnProgressListener = mOnProgressListener
    }

    interface OnFormatListener {

        fun format(progress: Int): String

    }
}
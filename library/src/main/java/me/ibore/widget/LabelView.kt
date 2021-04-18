package me.ibore.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import me.ibore.R
import me.ibore.ktx.dp2px
import me.ibore.ktx.sp2px
import me.ibore.utils.UIUtils
import java.util.*
import kotlin.math.sqrt


class LabelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {
    private var mTextContent: String? = null
    private var mTextColor = 0
    private var mTextSize = 0f
    private var mTextBold = false
    private var mFillTriangle = false
    private var mTextAllCaps = false
    private var mBackgroundColor = 0
    private var mMinSize = 0f
    private var mPadding = 0f

    /**
     * Gravity.TOP | Gravity.LEFT
     * Gravity.TOP | Gravity.RIGHT
     * Gravity.BOTTOM | Gravity.LEFT
     * Gravity.BOTTOM | Gravity.RIGHT
     */
    var gravity = 0
    private val mTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBackgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPath: Path = Path()


    init {
        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelView)
        mTextContent = ta.getString(R.styleable.LabelView_lvText)
        mTextColor = ta.getColor(R.styleable.LabelView_lvTextColor, Color.parseColor("#FFFFFF"))
        mTextSize = ta.getDimension(R.styleable.LabelView_lvTextSize, sp2px(11F).toFloat())
        mTextBold = ta.getBoolean(R.styleable.LabelView_lvTextBold, true)
        mTextAllCaps = ta.getBoolean(R.styleable.LabelView_lvTextAllCaps, true)
        mFillTriangle = ta.getBoolean(R.styleable.LabelView_lvFillTriangle, false)
        mBackgroundColor = ta.getColor(R.styleable.LabelView_lvBackgroundColor, Color.parseColor("#FF4081"))
        mMinSize = ta.getDimension(R.styleable.LabelView_lvMinSize, if (mFillTriangle) dp2px(35f).toFloat() else dp2px(50f).toFloat())
        mPadding = ta.getDimension(R.styleable.LabelView_lvPadding, dp2px(3.5f).toFloat())
        gravity = ta.getInt(R.styleable.LabelView_lvGravity, Gravity.TOP or Gravity.START)
        ta.recycle()
        mTextPaint.textAlign = Paint.Align.CENTER
    }

    var text: String?
        get() = mTextContent
        set(text) {
            mTextContent = text
            invalidate()
        }
    var textColor: Int
        get() = mTextColor
        set(textColor) {
            mTextColor = textColor
            invalidate()
        }
    var textSize: Float
        get() = mTextSize
        set(textSize) {
            mTextSize = sp2px(textSize).toFloat()
            invalidate()
        }
    var isTextBold: Boolean
        get() = mTextBold
        set(textBold) {
            mTextBold = textBold
            invalidate()
        }
    var isFillTriangle: Boolean
        get() = mFillTriangle
        set(fillTriangle) {
            mFillTriangle = fillTriangle
            invalidate()
        }
    var isTextAllCaps: Boolean
        get() = mTextAllCaps
        set(textAllCaps) {
            mTextAllCaps = textAllCaps
            invalidate()
        }
    var bgColor: Int
        get() = mBackgroundColor
        set(backgroundColor) {
            mBackgroundColor = backgroundColor
            invalidate()
        }
    var minSize: Float
        get() = mMinSize
        set(minSize) {
            mMinSize = dp2px(minSize).toFloat()
            invalidate()
        }
    var padding: Float
        get() = mPadding
        set(padding) {
            mPadding = dp2px(padding).toFloat()
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        val size = height.toFloat()
        mTextPaint.color = mTextColor
        mTextPaint.textSize = mTextSize
        mTextPaint.isFakeBoldText = mTextBold
        mBackgroundPaint.color = mBackgroundColor
        val textHeight: Float = mTextPaint.descent() - mTextPaint.ascent()
        if (mFillTriangle) {
            when (gravity) {
                Gravity.TOP or Gravity.START -> {
                    mPath.reset()
                    mPath.moveTo(0F, 0F)
                    mPath.lineTo(0F, size)
                    mPath.lineTo(size, 0F)
                    mPath.close()
                    canvas.drawPath(mPath, mBackgroundPaint)
                    drawTextWhenFill(size.toInt(), -DEFAULT_DEGREES.toFloat(), canvas, true)
                }
                Gravity.TOP or Gravity.END -> {
                    mPath.reset()
                    mPath.moveTo(size, 0F)
                    mPath.lineTo(0F, 0F)
                    mPath.lineTo(size, size)
                    mPath.close()
                    canvas.drawPath(mPath, mBackgroundPaint)
                    drawTextWhenFill(size.toInt(), DEFAULT_DEGREES.toFloat(), canvas, true)
                }
                Gravity.BOTTOM or Gravity.START -> {
                    mPath.reset()
                    mPath.moveTo(0F, size)
                    mPath.lineTo(0F, 0F)
                    mPath.lineTo(size, size)
                    mPath.close()
                    canvas.drawPath(mPath, mBackgroundPaint)
                    drawTextWhenFill(size.toInt(), DEFAULT_DEGREES.toFloat(), canvas, false)
                }
                Gravity.BOTTOM or Gravity.END -> {
                    mPath.reset()
                    mPath.moveTo(size, size)
                    mPath.lineTo(0F, size)
                    mPath.lineTo(size, 0F)
                    mPath.close()
                    canvas.drawPath(mPath, mBackgroundPaint)
                    drawTextWhenFill(size.toInt(), -DEFAULT_DEGREES.toFloat(), canvas, false)
                }
            }
        } else {
            val delta = (textHeight + mPadding * 2) * sqrt(2.0)
            when (gravity) {
                Gravity.TOP or Gravity.START -> {
                    mPath.reset()
                    mPath.moveTo(0F, (size - delta).toFloat())
                    mPath.lineTo(0F, size)
                    mPath.lineTo(size, 0F)
                    mPath.lineTo((size - delta).toFloat(), 0F)
                    mPath.close()
                    canvas.drawPath(mPath, mBackgroundPaint)
                    drawText(size.toInt(), -DEFAULT_DEGREES.toFloat(), canvas, textHeight, true)
                }
                Gravity.TOP or Gravity.END -> {
                    mPath.reset()
                    mPath.moveTo(0F, 0F)
                    mPath.lineTo(delta.toFloat(), 0F)
                    mPath.lineTo(size, (size - delta).toFloat())
                    mPath.lineTo(size, size)
                    mPath.close()
                    canvas.drawPath(mPath, mBackgroundPaint)
                    drawText(size.toInt(), DEFAULT_DEGREES.toFloat(), canvas, textHeight, true)
                }
                Gravity.BOTTOM or Gravity.START -> {
                    mPath.reset()
                    mPath.moveTo(0F, 0F)
                    mPath.lineTo(0F, delta.toFloat())
                    mPath.lineTo((size - delta).toFloat(), size)
                    mPath.lineTo(size, size)
                    mPath.close()
                    canvas.drawPath(mPath, mBackgroundPaint)
                    drawText(size.toInt(), DEFAULT_DEGREES.toFloat(), canvas, textHeight, false)
                }
                Gravity.BOTTOM or Gravity.END -> {
                    mPath.reset()
                    mPath.moveTo(0F, size)
                    mPath.lineTo(delta.toFloat(), size)
                    mPath.lineTo(size, delta.toFloat())
                    mPath.lineTo(size, 0F)
                    mPath.close()
                    canvas.drawPath(mPath, mBackgroundPaint)
                    drawText(size.toInt(), -DEFAULT_DEGREES.toFloat(), canvas, textHeight, false)
                }
            }
        }
    }

    private fun drawText(size: Int, degrees: Float, canvas: Canvas, textHeight: Float, isTop: Boolean) {
        canvas.save()
        canvas.rotate(degrees, size / 2f, size / 2f)
        val delta = if (isTop) -(textHeight + mPadding * 2) / 2 else (textHeight + mPadding * 2) / 2
        val textBaseY: Float = size / 2 - (mTextPaint.descent() + mTextPaint.ascent()) / 2 + delta
        canvas.drawText(if (mTextAllCaps) mTextContent!!.toUpperCase(Locale.ROOT) else mTextContent!!,
                paddingLeft + (size - paddingLeft - paddingRight) / 2F, textBaseY, mTextPaint)
        canvas.restore()
    }

    private fun drawTextWhenFill(size: Int, degrees: Float, canvas: Canvas, isTop: Boolean) {
        canvas.save()
        canvas.rotate(degrees, size / 2f, size / 2f)
        val delta = if (isTop) (-size / 4).toFloat() else size / 4.toFloat()
        val textBaseY: Float = size / 2 - (mTextPaint.descent() + mTextPaint.ascent()) / 2 + delta
        canvas.drawText(if (mTextAllCaps) mTextContent!!.toUpperCase(Locale.ROOT) else mTextContent!!,
                paddingLeft + (size - paddingLeft - paddingRight) / 2F, textBaseY, mTextPaint)
        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = measureWidth(widthMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }

    /** 确定View宽度大小  */
    private fun measureWidth(widthMeasureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(widthMeasureSpec)
        val specSize = MeasureSpec.getSize(widthMeasureSpec)
        if (specMode == MeasureSpec.EXACTLY) { //大小确定直接使用
            result = specSize
        } else {
            val padding: Int = paddingLeft + paddingRight
            mTextPaint.color = mTextColor
            mTextPaint.textSize = mTextSize
            val textWidth: Float = mTextPaint.measureText(mTextContent + "")
            result = ((padding + textWidth.toInt()) * Math.sqrt(2.0)).toInt()
            //如果父视图的测量要求为AT_MOST,即限定了一个最大值,则再从系统建议值和自己计算值中去一个较小值
            if (specMode == MeasureSpec.AT_MOST) {
                result = result.coerceAtMost(specSize)
            }
            result = mMinSize.toInt().coerceAtLeast(result)
        }
        return result
    }

    companion object {
        private const val DEFAULT_DEGREES = 45
    }


}
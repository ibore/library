package me.ibore.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import me.ibore.ktx.color
import kotlin.math.abs


class IndexBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val DEFAULT_INDEX_ITEMS = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")

    var mIndexItems: MutableList<String> = ArrayList()

    //每个index的高度
    private var mItemHeight = 0f
    //sp
    private var mTextSize = 0

    private var mTextColor = 0
    private var mTextTouchedColor = 0
    private var mCurrentIndex = -1

    private var mPaint: Paint
    private var mTouchedPaint: Paint

    private var mWidth = 0
    private var mHeight = 0
    //居中绘制，文字绘制起点和控件顶部的间隔
    private var mTopMargin = 0f

    private var mOverlayTextView: TextView? = null
    private var mOnIndexChangedListener: OnIndexChangedListener? = null
    private var navigationBarHeight = 0

    init {
        mIndexItems.addAll(DEFAULT_INDEX_ITEMS)
        val typedValue = TypedValue()
        //context.theme.resolveAttribute(R.attr.cpIndexBarTextSize, typedValue, true)
        mTextSize = context.resources.getDimensionPixelSize(typedValue.resourceId)
        //context.theme.resolveAttribute(R.attr.cpIndexBarNormalTextColor, typedValue, true)
        mTextColor = color(typedValue.resourceId)
        //context.theme.resolveAttribute(R.attr.cpIndexBarSelectedTextColor, typedValue, true)
        mTextTouchedColor = color(typedValue.resourceId)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        //mPaint.setTextSize(mTextSize)
        mPaint.color = mTextColor
        mTouchedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        //mTouchedPaint.setTextSize(mTextSize)
        mTouchedPaint.color = mTextTouchedColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var index: String
        for (i in mIndexItems.indices) {
            index = mIndexItems[i]
            val fm: Paint.FontMetrics = mPaint.getFontMetrics()
            canvas.drawText(index,
                    (mWidth - mPaint.measureText(index)) / 2,
                    mItemHeight / 2 + (fm.bottom - fm.top) / 2 - fm.bottom + mItemHeight * i + mTopMargin,
                    if (i == mCurrentIndex) mTouchedPaint else mPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = width
        mHeight = if (abs(h - oldh) == navigationBarHeight) { //底部导航栏隐藏或显示
            h
        } else { //避免软键盘弹出时挤压
            height.coerceAtLeast(oldh)
        }
        mItemHeight = mHeight / mIndexItems.size.toFloat()
        mTopMargin = (mHeight - mItemHeight * mIndexItems.size) / 2
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val y = event.y
                val indexSize = mIndexItems.size
                var touchedIndex = (y / mItemHeight).toInt()
                if (touchedIndex < 0) {
                    touchedIndex = 0
                } else if (touchedIndex >= indexSize) {
                    touchedIndex = indexSize - 1
                }
                if (mOnIndexChangedListener != null && touchedIndex >= 0 && touchedIndex < indexSize) {
                    if (touchedIndex != mCurrentIndex) {
                        mCurrentIndex = touchedIndex
                        if (mOverlayTextView != null) {
                            mOverlayTextView!!.visibility = VISIBLE
                            mOverlayTextView!!.text = mIndexItems[touchedIndex]
                        }
                        mOnIndexChangedListener!!.onIndexChanged(mIndexItems[touchedIndex], touchedIndex)
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mCurrentIndex = -1
                if (mOverlayTextView != null) {
                    mOverlayTextView!!.visibility = GONE
                }
                invalidate()
            }
        }
        return true
    }

    fun setNavigationBarHeight(height: Int) {
        navigationBarHeight = height
    }

    fun setOverlayTextView(overlay: TextView?): IndexBar {
        mOverlayTextView = overlay
        return this
    }

    fun setOnIndexChangedListener(listener: OnIndexChangedListener?): IndexBar {
        mOnIndexChangedListener = listener
        return this
    }

    interface OnIndexChangedListener {
        fun onIndexChanged(index: String, position: Int)
    }

}
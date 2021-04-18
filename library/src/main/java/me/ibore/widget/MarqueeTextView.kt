package me.ibore.widget

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import androidx.appcompat.widget.AppCompatTextView
import me.ibore.R


/**
 * 跑马灯效果的TextView, 使用方式：
 * 启动/关闭：{@link #setMarqueeEnable(boolean)}
 * xml文件中记得设置：android:focusable="true", android:singleLine="true"
 *
 * Created by dasu on 2017/3/21.
 * http://www.jianshu.com/u/bb52a2918096
 */

class MarqueeTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatTextView(context, attrs, defStyleAttr) {

    /** 默认滚动时间  */
    private val ROLLING_INTERVAL_DEFAULT = 10000

    /** 第一次滚动默认延迟  */
    private val FIRST_SCROLL_DELAY_DEFAULT = 1000

    /** 滚动模式-一直滚动  */
    val SCROLL_FOREVER = 100

    /** 滚动模式-只滚动一次  */
    val SCROLL_ONCE = 101

    /** 滚动器  */
    private var mScroller: Scroller? = null

    /** 滚动一次的时间  */
    private var mRollingInterval = 0

    /** 滚动的初始 X 位置  */
    private var mXPaused = 0

    /** 是否暂停  */
    private var mPaused = true

    /** 是否第一次  */
    private var mFirst = true

    /** 滚动模式  */
    private var mScrollMode = 0

    /** 初次滚动时间间隔  */
    private var mFirstScrollDelay = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView)
        mRollingInterval = typedArray.getInt(R.styleable.MarqueeTextView_scroll_interval, ROLLING_INTERVAL_DEFAULT)
        mScrollMode = typedArray.getInt(R.styleable.MarqueeTextView_scroll_mode, SCROLL_FOREVER)
        mFirstScrollDelay = typedArray.getInt(R.styleable.MarqueeTextView_scroll_first_delay, FIRST_SCROLL_DELAY_DEFAULT)
        typedArray.recycle()
        setSingleLine()
        ellipsize = null
    }

    /**
     * 开始滚动
     */
    fun startScroll() {
        mXPaused = 0
        mPaused = true
        mFirst = true
        resumeScroll()
    }

    /**
     * 继续滚动
     */
    fun resumeScroll() {
        if (!mPaused) return
        // 设置水平滚动
        setHorizontallyScrolling(true)
        // 使用 LinearInterpolator 进行滚动
        if (mScroller == null) {
            mScroller = Scroller(this.context, LinearInterpolator())
            setScroller(mScroller)
        }
        val scrollingLen = calculateScrollingLen()
        val distance = scrollingLen - mXPaused
        val duration = java.lang.Double.valueOf(mRollingInterval * distance * 1.00000
                / scrollingLen).toInt()
        if (mFirst) {
            Handler(Looper.getMainLooper()).postDelayed({
                mScroller!!.startScroll(mXPaused, 0, distance, 0, duration)
                invalidate()
                mPaused = false
            }, mFirstScrollDelay.toLong())
        } else {
            mScroller!!.startScroll(mXPaused, 0, distance, 0, duration)
            invalidate()
            mPaused = false
        }
    }

    /**
     * 暂停滚动
     */
    fun pauseScroll() {
        if (null == mScroller) return
        if (mPaused) return
        mPaused = true
        mXPaused = mScroller!!.currX
        mScroller!!.abortAnimation()
    }

    /**
     * 停止滚动，并回到初始位置
     */
    fun stopScroll() {
        if (null == mScroller) {
            return
        }
        mPaused = true
        mScroller!!.startScroll(0, 0, 0, 0, 0)
    }

    /**
     * 计算滚动的距离
     *
     * @return 滚动的距离
     */
    private fun calculateScrollingLen(): Int {
        val tp = paint
        val rect = Rect()
        val strTxt = text.toString()
        tp.getTextBounds(strTxt, 0, strTxt.length, rect)
        return rect.width()
    }

    override fun computeScroll() {
        super.computeScroll()
        if (null == mScroller) return
        if (mScroller!!.isFinished && !mPaused) {
            if (mScrollMode == SCROLL_ONCE) {
                stopScroll()
                return
            }
            mPaused = true
            mXPaused = -1 * width
            mFirst = false
            resumeScroll()
        }
    }

    /** 获取滚动一次的时间  */
    fun getRndDuration(): Int {
        return mRollingInterval
    }

    /** 设置滚动一次的时间  */
    fun setRndDuration(duration: Int) {
        mRollingInterval = duration
    }

    /** 设置滚动模式  */
    fun setScrollMode(mode: Int) {
        mScrollMode = mode
    }

    /** 获取滚动模式  */
    fun getScrollMode(): Int {
        return mScrollMode
    }

    /** 设置第一次滚动延迟  */
    fun setScrollFirstDelay(delay: Int) {
        mFirstScrollDelay = delay
    }

    /** 获取第一次滚动延迟  */
    fun getScrollFirstDelay(): Int {
        return mFirstScrollDelay
    }

    fun isPaused(): Boolean {
        return mPaused
    }

}
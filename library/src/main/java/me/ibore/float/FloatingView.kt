package me.ibore.float

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import me.ibore.utils.ScreenUtils
import me.ibore.utils.BarUtils

open class FloatingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val MARGIN_EDGE = 13
        private const val TOUCH_TIME_THRESHOLD = 150
    }

    private var mOriginalRawX = 0f
    private var mOriginalRawY = 0f
    private var mOriginalX = 0f
    private var mOriginalY = 0f
    private var mFloatingListener: FloatingListener? = null
    private var mLastTouchDownTime: Long = 0
    protected var mMoveAnimator: MoveAnimator? = null
    protected var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mStatusBarHeight = 0
    private var isNearestLeft = true

    fun setFloatingListener(floatingListener: FloatingListener?) {
        mFloatingListener = floatingListener
    }

    init {
        mMoveAnimator = MoveAnimator()
        mStatusBarHeight = BarUtils.getStatusBarHeight(getContext())
        isClickable = true
        updateSize()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                changeOriginalTouchParams(event)
                updateSize()
                mMoveAnimator!!.stop()
            }
            MotionEvent.ACTION_MOVE -> updateViewPosition(event)
            MotionEvent.ACTION_UP -> {
                moveToEdge()
                if (System.currentTimeMillis() - mLastTouchDownTime < TOUCH_TIME_THRESHOLD) {
                    dealClickEvent()
                }
            }
        }
        return true
    }

    protected fun dealClickEvent() {
        mFloatingListener?.onClick(this)
    }

    private fun updateViewPosition(event: MotionEvent) {
        x = mOriginalX + event.rawX - mOriginalRawX
        // 限制不可超出屏幕高度
        var desY = mOriginalY + event.rawY - mOriginalRawY
        if (desY < mStatusBarHeight) {
            desY = mStatusBarHeight.toFloat()
        }
        if (desY > mScreenHeight - height) {
            desY = mScreenHeight - height.toFloat()
        }
        y = desY
    }

    private fun changeOriginalTouchParams(event: MotionEvent) {
        mOriginalX = x
        mOriginalY = y
        mOriginalRawX = event.rawX
        mOriginalRawY = event.rawY
        mLastTouchDownTime = System.currentTimeMillis()
    }

    protected fun updateSize() {
        mScreenWidth = ScreenUtils.screenWidth - this.width
        mScreenHeight = ScreenUtils.screenHeight
    }

    @JvmOverloads
    fun moveToEdge(isLeft: Boolean = isNearestLeft()) {
        val moveDistance = if (isLeft) MARGIN_EDGE.toFloat() else mScreenWidth - MARGIN_EDGE.toFloat()
        mMoveAnimator!!.start(moveDistance, y)
    }

    protected fun isNearestLeft(): Boolean {
        val middle = mScreenWidth / 2
        isNearestLeft = x < middle
        return isNearestLeft
    }

    fun onRemove() {
        mFloatingListener?.onRemove(this)
    }

    protected inner class MoveAnimator : Runnable {
        private val handler = Handler(Looper.getMainLooper())
        private var destinationX = 0f
        private var destinationY = 0f
        private var startingTime: Long = 0
        fun start(x: Float, y: Float) {
            destinationX = x
            destinationY = y
            startingTime = System.currentTimeMillis()
            handler.post(this)
        }

        override fun run() {
            if (rootView == null || rootView.parent == null) return
            val progress = 1f.coerceAtMost((System.currentTimeMillis() - startingTime) / 400f)
            val deltaX = (destinationX - x) * progress
            val deltaY = (destinationY - y) * progress
            move(deltaX, deltaY)
            if (progress < 1) {
                handler.post(this)
            }
        }

        fun stop() {
            handler.removeCallbacks(this)
        }
    }

    private fun move(deltaX: Float, deltaY: Float) {
        x += deltaX
        y += deltaY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateSize()
        moveToEdge(isNearestLeft)
    }

}

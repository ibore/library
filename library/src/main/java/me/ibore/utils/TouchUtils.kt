package me.ibore.utils

import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewConfiguration
import androidx.annotation.IntDef

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2019/08/26
 * desc  : utils about touch
</pre> *
 */
object TouchUtils {

    const val UNKNOWN = 0
    const val LEFT = 1
    const val UP = 2
    const val RIGHT = 4
    const val DOWN = 8

    fun setOnTouchListener(v: View, listener: OnTouchUtilsListener) {
        v.setOnTouchListener(listener)
    }

    @IntDef(LEFT, UP, RIGHT, DOWN)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Direction
    abstract class OnTouchUtilsListener : OnTouchListener {
        private var touchSlop = 0
        private var downX = 0
        private var downY = 0
        private var lastX = 0
        private var lastY = 0
        private var state = 0
        private var direction = 0
        private var velocityTracker: VelocityTracker? = null
        private var maximumFlingVelocity = 0
        private var minimumFlingVelocity = 0
        private fun resetTouch(x: Int, y: Int) {
            downX = x
            downY = y
            lastX = x
            lastY = y
            state = STATE_DOWN
            direction = UNKNOWN
            if (velocityTracker != null) {
                velocityTracker!!.clear()
            }
        }

        abstract fun onDown(view: View?, x: Int, y: Int, event: MotionEvent?): Boolean
        abstract fun onMove(
            view: View?, @Direction direction: Int,
            x: Int, y: Int, dx: Int, dy: Int, totalX: Int, totalY: Int, event: MotionEvent?
        ): Boolean

        abstract fun onStop(
            view: View?, @Direction direction: Int,
            x: Int, y: Int, totalX: Int, totalY: Int, vx: Int, vy: Int, event: MotionEvent?
        ): Boolean

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (touchSlop == 0) {
                touchSlop = ViewConfiguration.get(v.context).scaledTouchSlop
            }
            if (maximumFlingVelocity == 0) {
                maximumFlingVelocity = ViewConfiguration.get(v.context).scaledMaximumFlingVelocity
            }
            if (minimumFlingVelocity == 0) {
                minimumFlingVelocity = ViewConfiguration.get(v.context).scaledMinimumFlingVelocity
            }
            if (velocityTracker == null) {
                velocityTracker = VelocityTracker.obtain()
            }
            velocityTracker!!.addMovement(event)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> return onUtilsDown(v, event)
                MotionEvent.ACTION_MOVE -> return onUtilsMove(v, event)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> return onUtilsStop(v, event)
                else -> {
                }
            }
            return false
        }

        fun onUtilsDown(view: View, event: MotionEvent): Boolean {
            val x = event.rawX.toInt()
            val y = event.rawY.toInt()
            resetTouch(x, y)
            view.isPressed = true
            return onDown(view, x, y, event)
        }

        fun onUtilsMove(view: View, event: MotionEvent): Boolean {
            val x = event.rawX.toInt()
            val y = event.rawY.toInt()
            if (downX == -1) {
                // not receive down should reset
                resetTouch(x, y)
                view.isPressed = true
            }
            if (state != STATE_MOVE) {
                if (Math.abs(x - lastX) < touchSlop && Math.abs(y - lastY) < touchSlop) {
                    return true
                }
                state = STATE_MOVE
                direction = if (Math.abs(x - lastX) >= Math.abs(y - lastY)) {
                    if (x - lastX < 0) {
                        LEFT
                    } else {
                        RIGHT
                    }
                } else {
                    if (y - lastY < 0) {
                        UP
                    } else {
                        DOWN
                    }
                }
            }
            val consumeMove =
                onMove(view, direction, x, y, x - lastX, y - lastY, x - downX, y - downY, event)
            lastX = x
            lastY = y
            return consumeMove
        }

        fun onUtilsStop(view: View, event: MotionEvent): Boolean {
            val x = event.rawX.toInt()
            val y = event.rawY.toInt()
            var vx = 0
            var vy = 0
            if (velocityTracker != null) {
                velocityTracker!!.computeCurrentVelocity(1000, maximumFlingVelocity.toFloat())
                vx = velocityTracker!!.xVelocity.toInt()
                vy = velocityTracker!!.yVelocity.toInt()
                velocityTracker!!.recycle()
                if (Math.abs(vx) < minimumFlingVelocity) {
                    vx = 0
                }
                if (Math.abs(vy) < minimumFlingVelocity) {
                    vy = 0
                }
                velocityTracker = null
            }
            view.isPressed = false
            val consumeStop = onStop(view, direction, x, y, x - downX, y - downY, vx, vy, event)
            if (event.action == MotionEvent.ACTION_UP) {
                if (state == STATE_DOWN) {
                    if (event.eventTime - event.downTime <= MIN_TAP_TIME) {
                        view.performClick()
                    } else {
                        view.performLongClick()
                    }
                }
            }
            resetTouch(-1, -1)
            return consumeStop
        }

        companion object {
            private const val STATE_DOWN = 0
            private const val STATE_MOVE = 1
            private const val STATE_STOP = 2
            private const val MIN_TAP_TIME = 1000
        }

        init {
            resetTouch(-1, -1)
        }
    }

}
package me.ibore.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/08/02
 * desc  : utils about size
</pre> *
 */
object SizeUtils {

    /**
     * dp 转 px
     *
     * @param context 上下文
     * @param dpValue dp 值
     * @return px 值
     */
    @JvmStatic
    @JvmOverloads
    fun dp2px(dpValue: Float, context: Context = Utils.app): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px 转 dp
     *
     * @param pxValue px 值
     * @return dp 值
     */
    @JvmStatic
    @JvmOverloads
    fun px2dp( pxValue: Int, context: Context = Utils.app): Float {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f)
    }

    /**
     * sp 转 px
     *
     * @param spValue sp 值
     * @return px 值
     */
    @JvmStatic
    @JvmOverloads
    fun sp2px( spValue: Float, context: Context = Utils.app): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * px 转 sp
     *
     * @param pxValue px 值
     * @return sp 值
     */
    @JvmStatic
    @JvmOverloads
    fun px2sp(pxValue: Int, context: Context = Utils.app): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f)
    }

    /**
     * Converts an unpacked complex data value holding a dimension to its final floating
     * point value. The two parameters <var>unit</var> and <var>value</var>
     * are as in [TypedValue.TYPE_DIMENSION].
     *
     * @param value The value to apply the unit to.
     * @param unit  The unit to convert from.
     * @return The complex floating point value multiplied by the appropriate
     * metrics depending on its unit.
     */
    fun applyDimension(value: Float, unit: Int): Float {
        val metrics = Resources.getSystem().displayMetrics
        when (unit) {
            TypedValue.COMPLEX_UNIT_PX -> return value
            TypedValue.COMPLEX_UNIT_DIP -> return value * metrics.density
            TypedValue.COMPLEX_UNIT_SP -> return value * metrics.scaledDensity
            TypedValue.COMPLEX_UNIT_PT -> return value * metrics.xdpi * (1.0f / 72)
            TypedValue.COMPLEX_UNIT_IN -> return value * metrics.xdpi
            TypedValue.COMPLEX_UNIT_MM -> return value * metrics.xdpi * (1.0f / 25.4f)
        }
        return 0F
    }

    /**
     * Force get the size of view.
     *
     * e.g.
     * <pre>
     * SizeUtils.forceGetViewSize(view, new SizeUtils.OnGetSizeListener() {
     * Override
     * public void onGetSize(final View view) {
     * view.getWidth();
     * }
     * });
    </pre> *
     *
     * @param view     The view.
     * @param listener The get size listener.
     */
    fun forceGetViewSize(view: View, listener: OnGetSizeListener?) {
        view.post { listener?.onGetSize(view) }
    }

    /**
     * Return the width of view.
     *
     * @param view The view.
     * @return the width of view
     */
    fun getMeasuredWidth(view: View): Int {
        return measureView(view)[0]
    }

    /**
     * Return the height of view.
     *
     * @param view The view.
     * @return the height of view
     */
    fun getMeasuredHeight(view: View): Int {
        return measureView(view)[1]
    }

    /**
     * Measure the view.
     *
     * @param view The view.
     * @return arr[0]: view's width, arr[1]: view's height
     */
    fun measureView(view: View): IntArray {
        var lp = view.layoutParams
        if (lp == null) {
            lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width)
        val lpHeight = lp.height
        val heightSpec: Int = if (lpHeight > 0) {
            View.MeasureSpec.makeMeasureSpec(
                lpHeight,
                View.MeasureSpec.EXACTLY
            )
        } else {
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        }
        view.measure(widthSpec, heightSpec)
        return intArrayOf(view.measuredWidth, view.measuredHeight)
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    interface OnGetSizeListener {
        fun onGetSize(view: View?)
    }
}
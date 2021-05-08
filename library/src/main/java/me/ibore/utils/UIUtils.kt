@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package me.ibore.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import me.ibore.ktx.dp2px

object UIUtils {


    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val activity: Activity = if (context !is Activity && context is ContextWrapper) {
            context.baseContext as Activity
        } else {
            context as Activity
        }
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        return metrics
    }

    /**
     * 获取屏幕大小
     *
     * @param context
     * @return
     */
    fun getScreenPixelSize(context: Context): IntArray {
        val metrics = getDisplayMetrics(context)
        return intArrayOf(metrics.widthPixels, metrics.heightPixels)
    }

    fun hideSoftInputKeyBoard(context: Context, focusView: View?) {
        if (focusView != null) {
            val binder = focusView.windowToken
            if (binder != null) {
                val imd = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imd.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
        }
    }

    fun showSoftInputKeyBoard(context: Context, focusView: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(focusView, InputMethodManager.SHOW_FORCED)
    }

    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    @SuppressLint("PrivateApi")
    fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = 0
        try {
            val c = Class.forName("com.android.internal.R\$dimen")
            val obj = c.newInstance()
            val field = c.getField("status_bar_height")
            val x = Integer.parseInt(field.get(obj).toString())
            statusBarHeight = context.resources.getDimensionPixelSize(x)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return statusBarHeight
    }

    @JvmOverloads
    fun getCornerRadii(context: Context, topStartRadius: Float, topEndRadius: Float,
                       bottomEndRadius: Float, bottomStartRadius: Float, dp: Boolean = true): FloatArray {
        val topStartRadiusTemp = if (dp) context.dp2px( topStartRadius).toFloat() else topStartRadius
        val topEndRadiusTemp = if (dp) context.dp2px( topEndRadius).toFloat() else topEndRadius
        val bottomEndRadiusTemp = if (dp) context.dp2px( bottomEndRadius).toFloat() else bottomEndRadius
        val bottomStartRadiusTemp = if (dp) context.dp2px(bottomStartRadius).toFloat() else bottomStartRadius
        return floatArrayOf(topStartRadiusTemp, topStartRadiusTemp, topEndRadiusTemp, topEndRadiusTemp,
                bottomEndRadiusTemp, bottomEndRadiusTemp, bottomStartRadiusTemp, bottomStartRadiusTemp)
    }

    @JvmOverloads
    fun setPadding(view: View, padding: Float, dp: Boolean = true) {
        val paddingTemp: Int = if (dp) view.dp2px(padding) else padding.toInt()
        view.setPadding(paddingTemp, paddingTemp, paddingTemp, paddingTemp)
    }

    @JvmOverloads
    fun setPadding(view: View, paddingStart: Float, paddingTop: Float, paddingEnd: Float, paddingBottom: Float, dp: Boolean = true) {
        val paddingStartTemp: Int = if (dp) view.dp2px(paddingStart) else paddingStart.toInt()
        val paddingTopTemp: Int = if (dp) view.dp2px(paddingTop) else paddingTop.toInt()
        val paddingEndTemp: Int = if (dp) view.dp2px(paddingEnd) else paddingEnd.toInt()
        val paddingBottomTemp: Int = if (dp) view.dp2px(paddingBottom) else paddingBottom.toInt()
        view.setPadding(paddingStartTemp, paddingTopTemp, paddingEndTemp, paddingBottomTemp)
    }

    fun setMargin(view: View, left: Float, top: Float, right: Float, bottom: Float, dp: Boolean = true) {
        val leftTemp: Int = if (dp) view.dp2px(left) else left.toInt()
        val topTemp: Int = if (dp) view.dp2px(top) else top.toInt()
        val rightTemp: Int = if (dp) view.dp2px(right) else right.toInt()
        val bottomTemp: Int = if (dp) view.dp2px(bottom) else bottom.toInt()
        val layoutParams: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(leftTemp, topTemp, rightTemp, bottomTemp)
        view.layoutParams = layoutParams
    }

    fun setMargin(context: Context, layoutParams: ViewGroup.MarginLayoutParams, left: Float, top: Float, right: Float, bottom: Float, dp: Boolean = true) {
        val leftTemp: Int = if (dp) context.dp2px( left) else left.toInt()
        val topTemp: Int = if (dp) context.dp2px( top) else top.toInt()
        val rightTemp: Int = if (dp) context.dp2px( right) else right.toInt()
        val bottomTemp: Int = if (dp) context.dp2px( bottom) else bottom.toInt()
        layoutParams.setMargins(leftTemp, topTemp, rightTemp, bottomTemp)
    }

    fun getWarpParams(): ViewGroup.LayoutParams {
        return ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    }


}
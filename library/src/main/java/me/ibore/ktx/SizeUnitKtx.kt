package me.ibore.ktx

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.ibore.utils.Utils

/**
 * @Author: QuYunShuo
 * @Time: 2020/9/17
 * @Class: SizeUnitKtx
 * @Remark: 尺寸单位换算相关扩展属性
 */

/**
 * dp 转 px
 */
fun Context.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

/**
 * px 转 dp
 */
fun Context.px2dp(pxValue: Int): Float {
    val scale = resources.displayMetrics.density
    return pxValue / scale + 0.5f
}

/**
 * sp 转 px
 */
fun Context.sp2px(spValue: Float): Int {
    val scale = resources.displayMetrics.scaledDensity
    return (spValue * scale + 0.5f).toInt()
}

/**
 * px 转 sp
 */
fun Context.px2sp(pxValue: Int): Float {
    val scale = resources.displayMetrics.scaledDensity
    return pxValue / scale + 0.5f
}

/**
 * dp 转 px
 */
fun View.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

/**
 * px 转 dp
 */
fun View.px2dp(pxValue: Int): Float {
    val scale = resources.displayMetrics.density
    return pxValue / scale + 0.5f
}

/**
 * sp 转 px
 */
fun View.sp2px(spValue: Float): Int {
    val scale = resources.displayMetrics.scaledDensity
    return (spValue * scale + 0.5f).toInt()
}

/**
 * px 转 sp
 */
fun View.px2sp(pxValue: Int): Float {
    val scale = resources.displayMetrics.scaledDensity
    return pxValue / scale + 0.5f
}

/**
 * dp 转 px
 */
fun ViewGroup.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

/**
 * px 转 dp
 */
fun ViewGroup.px2dp(pxValue: Int): Float {
    val scale = resources.displayMetrics.density
    return pxValue / scale + 0.5f
}

/**
 * sp 转 px
 */
fun ViewGroup.sp2px(spValue: Float): Int {
    val scale = resources.displayMetrics.scaledDensity
    return (spValue * scale + 0.5f).toInt()
}

/**
 * px 转 sp
 */
fun ViewGroup.px2sp(pxValue: Int): Float {
    val scale = resources.displayMetrics.scaledDensity
    return pxValue / scale + 0.5f
}

/**
 * dp 转 px
 */
fun Fragment.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

/**
 * px 转 dp
 */
fun Fragment.px2dp(pxValue: Int): Float {
    val scale = resources.displayMetrics.density
    return pxValue / scale + 0.5f
}

/**
 * sp 转 px
 */
fun Fragment.sp2px(spValue: Float): Int {
    val scale = resources.displayMetrics.scaledDensity
    return (spValue * scale + 0.5f).toInt()
}

/**
 * px 转 sp
 */
fun Fragment.px2sp(pxValue: Int): Float {
    val scale = resources.displayMetrics.scaledDensity
    return pxValue / scale + 0.5f
}

/**
 * dp 转 px
 */
fun Any.dp2px(dpValue: Float): Int {
    val scale = Utils.app.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

/**
 * px 转 dp
 */
fun Any.px2dp(pxValue: Int): Float {
    val scale = Utils.app.resources.displayMetrics.density
    return pxValue / scale + 0.5f
}

/**
 * sp 转 px
 */
fun Any.sp2px(spValue: Float): Int {
    val scale = Utils.app.resources.displayMetrics.scaledDensity
    return (spValue * scale + 0.5f).toInt()
}

/**
 * px 转 sp
 */
fun Any.px2sp(pxValue: Int): Float {
    val scale = Utils.app.resources.displayMetrics.scaledDensity
    return pxValue / scale + 0.5f
}

fun Context.screenWidth(): Int {
    return resources.displayMetrics.widthPixels
}

fun Context.screenHeight(): Int {
    return resources.displayMetrics.heightPixels
}


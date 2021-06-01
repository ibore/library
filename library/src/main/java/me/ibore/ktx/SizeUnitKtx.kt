package me.ibore.ktx

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.ibore.utils.ScreenUtils
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
fun Context.dp2px(dp: Float): Int {
    return dp2px(dp, this)
}

/**
 * px 转 dp
 */
fun Context.px2dp(px: Int): Float {
    return px2dp(px, this)
}

/**
 * sp 转 px
 */
fun Context.sp2px(sp: Float): Int {
    return sp2px(sp, this)
}

/**
 * px 转 sp
 */
fun Context.px2sp(px: Int): Float {
    return px2sp(px, this)
}

/**
 * dp 转 px
 */
fun View.dp2px(dp: Float): Int {
    return dp2px(dp, this.context)
}

/**
 * px 转 dp
 */
fun View.px2dp(px: Int): Float {
    return px2dp(px, this.context)
}

/**
 * sp 转 px
 */
fun View.sp2px(sp: Float): Int {
    return sp2px(sp, this.context)
}

/**
 * px 转 sp
 */
fun View.px2sp(px: Int): Float {
    return px2sp(px, this.context)
}

/**
 * dp 转 px
 */
fun ViewGroup.dp2px(dp: Float): Int {
    return dp2px(dp, this.context)
}

/**
 * px 转 dp
 */
fun ViewGroup.px2dp(px: Int): Float {
    return px2dp(px, this.context)
}

/**
 * sp 转 px
 */
fun ViewGroup.sp2px(sp: Float): Int {
    return sp2px(sp, this.context)
}

/**
 * px 转 sp
 */
fun ViewGroup.px2sp(px: Int): Float {
    return px2sp(px, this.context)
}
/**
 * dp 转 px
 */
fun Fragment.dp2px(dp: Float): Int {
    return dp2px(dp, this.requireContext())
}

/**
 * px 转 dp
 */
fun Fragment.px2dp(px: Int): Float {
    return px2dp(px, this.requireContext())
}

/**
 * sp 转 px
 */
fun Fragment.sp2px(sp: Float): Int {
    return sp2px(sp, this.requireContext())
}

/**
 * px 转 sp
 */
fun Fragment.px2sp(px: Int): Float {
    return px2sp(px, this.requireContext())
}

/**
 * dp 转 px
 */
fun dp2px(dp: Float, context: Context = Utils.app): Int {
    val scale = context.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

/**
 * px 转 dp
 */
fun px2dp(px: Int, context: Context = Utils.app): Float {
    val scale = context.resources.displayMetrics.density
    return px / scale + 0.5f
}

/**
 * sp 转 px
 */
fun sp2px(sp: Float, context: Context = Utils.app): Int {
    val scale = context.resources.displayMetrics.scaledDensity
    return (sp * scale + 0.5f).toInt()
}

/**
 * px 转 sp
 */
fun px2sp(px: Int, context: Context = Utils.app): Float {
    val scale = context.resources.displayMetrics.scaledDensity
    return px / scale + 0.5f
}

fun Context.screenWidth(): Int {
    return ScreenUtils.getAppScreenWidth(this)
}

fun Context.screenHeight(): Int {
    return ScreenUtils.getAppScreenHeight(this)
}


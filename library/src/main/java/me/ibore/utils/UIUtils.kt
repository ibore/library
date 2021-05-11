@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package me.ibore.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import me.ibore.ktx.dp2px

object UIUtils {



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


}
package me.ibore.utils

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import me.ibore.ktx.color
import me.ibore.utils.UIUtils.getCornerRadii

object DrawableUtils {

    fun getGradientRes(context: Context, @ColorRes color: Int, radius: Float, dp: Boolean = true): GradientDrawable {
        return getGradient(context, context.color(color), radius, radius, radius, radius, dp)
    }

    fun getGradientRes(context: Context, @ColorRes color: Int, topStartRadius: Float, topEndRadius: Float,
                       bottomEndRadius: Float, bottomStartRadius: Float, dp: Boolean = true): GradientDrawable {
        return getGradient(
            context, context.color(color),
            topStartRadius, topEndRadius, bottomEndRadius, bottomStartRadius, dp
        )
    }

    fun getGradient(context: Context, @ColorInt color: Int, radius: Float, dp: Boolean = true): GradientDrawable {
        return getGradient(context, color, radius, radius, radius, radius, dp)
    }

    fun getGradient(context: Context, @ColorInt color: Int, topStartRadius: Float, topEndRadius: Float,
                    bottomEndRadius: Float, bottomStartRadius: Float, dp: Boolean = true): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.cornerRadii = getCornerRadii(context, topStartRadius, topEndRadius, bottomEndRadius, bottomStartRadius, dp)
        drawable.setColor(color)
        return drawable
    }

    fun getStateListRes(context: Context, @ColorRes normalColor: Int, @ColorRes pressedColor: Int, radius: Float, dp: Boolean = true): StateListDrawable {
        return getStateList(
            context, context.color(normalColor),
            context.color(pressedColor), radius, radius, radius, radius, dp
        )
    }

    fun getStateListRes(context: Context, @ColorRes normalColor: Int, @ColorRes pressedColor: Int, topStartRadius: Float, topEndRadius: Float,
                        bottomEndRadius: Float, bottomStartRadius: Float, dp: Boolean = true): StateListDrawable {
        return getStateList(
            context, context.color(normalColor), context.color(pressedColor),
            topStartRadius, topEndRadius, bottomEndRadius, bottomStartRadius, dp
        )
    }

    fun getStateList(context: Context, @ColorInt normalColor: Int, @ColorInt pressedColor: Int, radius: Float, dp: Boolean = true): StateListDrawable {
        return getStateList(context, normalColor, pressedColor, radius, radius, radius, radius, dp)
    }

    fun getStateList(context: Context, @ColorInt normalColor: Int, @ColorInt pressedColor: Int, topStartRadius: Float, topEndRadius: Float,
                     bottomEndRadius: Float, bottomStartRadius: Float, dp: Boolean = true): StateListDrawable {
        val drawablePressed = GradientDrawable()
        val drawableNormal = GradientDrawable()
        val stateListDrawable = StateListDrawable()

        drawablePressed.setColor(pressedColor)
        drawableNormal.setColor(normalColor)

        drawableNormal.cornerRadii = getCornerRadii(context,
                topStartRadius, topEndRadius, bottomEndRadius, bottomStartRadius, dp)
        drawablePressed.cornerRadii = getCornerRadii(context,
                topStartRadius, topEndRadius, bottomEndRadius, bottomStartRadius, dp)

        stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), drawablePressed)
        stateListDrawable.addState(intArrayOf(android.R.attr.state_selected), drawablePressed)
        stateListDrawable.addState(intArrayOf(), drawableNormal)

        return stateListDrawable
    }

}